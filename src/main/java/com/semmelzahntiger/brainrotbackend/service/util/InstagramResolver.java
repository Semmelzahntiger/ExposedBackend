package com.semmelzahntiger.brainrotbackend.service.util;

import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.InstagramMediaItem;
import com.semmelzahntiger.brainrotbackend.util.Constants;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem.MediaType;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.MissingNode;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Big Credits to yt-dlp for the internal mechanism
@Service("instagram_resolver")
@Slf4j
public class InstagramResolver implements CdnResolver {

    private static final String INSTAGRAM_URL = "https://www.instagram.com";

    // matches: <script ... data-sjs>{...}</script>  (DOTALL so JSON can span newlines)
    private static final Pattern SJS_RE =
            Pattern.compile("<script\\b[^>]+\\bdata-sjs>(\\{.+?})</script>", Pattern.DOTALL);

    private final OkHttpClient httpClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public InstagramResolver() {
        httpClient = new OkHttpClient.Builder().build();
    }

    // ---- media type the RN client needs to know which component to mount ----


    // ---------------------------------------------------------------------
    // entry point
    // ---------------------------------------------------------------------
    public AbstractMediaItem getCdnContents(String urlStr) throws Exception {
        URI uri = new URI(urlStr);
        String[] segments = uri.getPath().split("/"); // segments[0] == ""

        if (segments.length < 3) {
            log.error("No shortcode found in URL: {}", urlStr);
            return null;
        }

        String type = segments[1]; // "reel", "reels", "p", "tv"
        String code = segments[2];

        return switch (type) {
            case "reel", "reels", "p", "tv" -> resolve(code);
            default -> {
                log.error("Unknown Instagram type '{}' in {}", type, urlStr);
                yield null;
            }
        };
    }

    // reels and posts both resolve via the /p/ page; only the JSON shape differs, not the path.
    private AbstractMediaItem resolve(String code) {
        try {
            WebpageResult page = downloadWebpageHandle(INSTAGRAM_URL + "/p/" + code + "/");
            if (page.finalUrl().contains("/accounts/login/")) {
                log.warn("Redirected to login for {} — anonymous rate-limit likely reached", code);
                return null;
            }

            JsonNode media = extractHydratedMedia(page.content());
            return collectFromNode(media);

        } catch (IOException e) {
            log.error("Fetch failed for {}", code, e);
            return null;
        }
    }

    // ---------------------------------------------------------------------
    // JSON walking
    // ---------------------------------------------------------------------

    /** Scan every data-sjs block for the first object that looks like hydrated media. */
    private JsonNode extractHydratedMedia(String webpage) {
        Matcher m = SJS_RE.matcher(webpage);
        while (m.find()) {
            JsonNode root;
            try {
                root = mapper.readTree(m.group(1));
            } catch (Exception e) {
                continue; // this block isn't valid/relevant JSON; skip it
            }
            JsonNode found = deepFindHydratedMedia(root);
            if (!found.isMissingNode()) {
                return found;
            }
        }
        return MissingNode.getInstance();
    }

    /**
     * Recursively search for ANY object that looks like hydrated media, regardless of the
     * key holding it. Searching by shape (not by a fixed key name or path) is durable
     * against Instagram reshaping the __bbox nesting, and skips the thin stub nodes.
     */
    private JsonNode deepFindHydratedMedia(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return MissingNode.getInstance();
        }
        if (node.isObject()) {
            if (isHydratedMedia(node)) {
                return node;
            }
            for (JsonNode child : node) {
                JsonNode r = deepFindHydratedMedia(child);
                if (!r.isMissingNode()) return r;
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                JsonNode r = deepFindHydratedMedia(child);
                if (!r.isMissingNode()) return r;
            }
        }
        return MissingNode.getInstance();
    }

    /** A real, playable media node carries these; thin timeline stubs do not. */
    private boolean isHydratedMedia(JsonNode m) {
        return m.has("video_versions")
                || m.has("video_dash_manifest")
                || m.has("carousel_media")
                || (m.has("image_versions2") && m.has("media_type"));
    }

    /** Dispatch: carousel -> each child; otherwise a single item. */
    private AbstractMediaItem collectFromNode(JsonNode node) {
        List<InstagramMediaItem.InstagramCDNEntry> entries = new ArrayList<>();

        JsonNode carousel = node.path("carousel_media");
        if (carousel.isArray() && !carousel.isEmpty()) {
            for (JsonNode child : carousel) {
                collectSingle(child, entries);
            }
        } else {
            collectSingle(node, entries);
        }
        if(entries.isEmpty()) {
            return null;
        }
        return new InstagramMediaItem(MediaType.MIXED, entries);
    }

    /** Extract one item: prefer the progressive (muxed) video, else the best image. */
    private void collectSingle(JsonNode node, List<InstagramMediaItem.InstagramCDNEntry> entries) {
        int mediaType = node.path("media_type").asInt(-1); // 1=image, 2=video, 8=carousel
        boolean isVideo = mediaType == 2
                || node.path("video_versions").isArray()
                || node.has("video_dash_manifest");

        if (isVideo) {
            String v = pickProgressiveVideo(node);
            if (v != null) {
                entries.add(new InstagramMediaItem.InstagramCDNEntry(MediaType.VIDEO, v));
            } else {
                log.warn("Video node had no progressive URL (DASH-only); skipping");
            }
            return;
        }
        String img = pickBestImage(node);
        if(img != null) {
            entries.add(new InstagramMediaItem.InstagramCDNEntry(MediaType.IMAGE, img));
        }
    }

    private String pickProgressiveVideo(JsonNode node) {
        JsonNode versions = node.path("video_versions");
        String firstUrl = null;
        if (versions.isArray()) {
            for (JsonNode v : versions) {
                String url = v.path("url").asText(null);
                if (url == null) continue;
                if (firstUrl == null) firstUrl = url;
                if (isProgressive(url)) return url; // efg vencode_tag contains "progressive"
            }
        }
        return firstUrl; // all entries are typically the same progressive URL anyway
    }

    /** Decode the efg query param -> vencode_tag, check for "progressive". */
    private boolean isProgressive(String url) {
        try {
            String efg = queryParam(url, "efg");
            if (efg == null) return false;
            int pad = (4 - efg.length() % 4) % 4;
            efg = efg + "=".repeat(pad);
            String json = new String(Base64.getDecoder().decode(efg));
            JsonNode tag = mapper.readTree(json).path("vencode_tag");
            return tag.asText("").contains("progressive");
        } catch (Exception e) {
            return false;
        }
    }

    /** Highest-resolution image from image_versions2.candidates. */
    private String pickBestImage(JsonNode node) {
        JsonNode candidates = node.path("image_versions2").path("candidates");
        String bestUrl = null;
        long bestArea = -1;
        if (candidates.isArray()) {
            for (JsonNode c : candidates) {
                long area = (long) c.path("width").asInt(0) * c.path("height").asInt(0);
                if (area > bestArea) {
                    bestArea = area;
                    bestUrl = c.path("url").asText(null);
                }
            }
        }
        if (bestUrl == null) {
            bestUrl = node.path("display_url").asText(null);
        }
        return bestUrl;
    }

    private static String queryParam(String url, String key) {
        int q = url.indexOf('?');
        if (q < 0) return null;
        for (String pair : url.substring(q + 1).split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && pair.substring(0, eq).equals(key)) {
                return pair.substring(eq + 1);
            }
        }
        return null;
    }

    // ---------------------------------------------------------------------
    // HTTP
    // ---------------------------------------------------------------------

    public WebpageResult downloadWebpageHandle(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/145.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-us,en;q=0.5")
                .header("Sec-Fetch-Mode", "navigate")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String content = response.body().string();
            String finalUrl = response.request().url().toString();
            return new WebpageResult(content, finalUrl);
        }
    }
}