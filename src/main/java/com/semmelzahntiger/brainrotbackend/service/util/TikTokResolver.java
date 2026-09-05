package com.semmelzahntiger.brainrotbackend.service.util;

import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.InMemoryCookieJar;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.TikTokSlideshowItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.TikTokVideoItem;
import com.semmelzahntiger.brainrotbackend.util.BlockBusterHeaderUtil;
import com.semmelzahntiger.brainrotbackend.util.Constants;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.TestOnly;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.MissingNode;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("tiktok_resolver")
@Slf4j
// Big Credits go to yt-dlp for the internal mechanism
public class TikTokResolver implements CdnResolver {
    public final ObjectMapper mapper = new ObjectMapper();
    public static final Pattern UNIVERSAL_DATA_RE = Pattern.compile(
            "<script[^>]+\\bid=\"__UNIVERSAL_DATA_FOR_REHYDRATION__\"[^>]*>(.*?)</script>",
            Pattern.DOTALL);
    public static final Pattern SIGI_STATE_RE = Pattern.compile(
            "<script[^>]+\\bid=\"(?:SIGI_STATE|sigi-persisted-data)\"[^>]*>(.*?)</script>",
            Pattern.DOTALL);

    public static final Pattern NEXT_DATA_RE = Pattern.compile(
            "<script[^>]+\\bid=\"__NEXT_DATA__\"[^>]*>(.*?)</script>",
            Pattern.DOTALL);

    public final OkHttpClient httpClient = new OkHttpClient.Builder()
            .cookieJar(new InMemoryCookieJar())
            .build();

    public TikTokResolver() {

    }
    @Override
    public AbstractMediaItem getCdnContents(String url) throws Exception {
        URI uri = new URI(url);
        String[] segments = uri.getPath().split("/");
        if (segments.length < 4) {
            log.error("Invalid URL, no Code found, Segments too few");
            return null;
        }
        String userId = segments[1];
        String type = segments[2];
        String shortCode = segments[3];

        return switch (type) {
            case "video" -> resolveVideo(userId, shortCode);
            case "photo" -> resolvePhoto(userId, shortCode);
            default -> null;
        };
    }

    public WebpageResult requestWebPage(String url) throws IOException {
        Map<String, String> headers = BlockBusterHeaderUtil.generateBlockbusterHeaders();

        Request.Builder requestBuilder = new Request.Builder()
                .url(url);
        headers.forEach(requestBuilder::header);
        Request request = requestBuilder
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/151.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-us,en;q=0.5")
                .header("Sec-Fetch-Mode", "navigate")
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            log.debug("------- COOKIE JAR -------");
            String ttChainToken = "";
            for (Cookie c : ((InMemoryCookieJar) httpClient.cookieJar()).loadForRequest(
                    HttpUrl.parse("https://www.tiktok.com/"))) {
                log.debug(c.value());
                if (c.name().equals("tt_chain_token")) {
                    ttChainToken = c.value();
                    log.info("tt_chain_token={}", c.value());
                }
            }
            log.debug("------- CLOSED JAR -------");
            String content = response.body().string();
            String finalUrl = response.request().url().toString();
            return new WebpageResult(content, finalUrl, ttChainToken);
        }
    }

    public AbstractMediaItem resolveVideo(String userId, String shortCode) {
        String link = reconstructLink(userId, "video", shortCode);
        try {
            return extractVideoData(link, shortCode);

        } catch (IOException e) {
            log.error("IO Exception occurred while resolving", e);
            return null;
        }
    }
    public AbstractMediaItem resolvePhoto(String userId, String shortCode) {
        String link = reconstructLink(userId, "video", shortCode);
        try {
            return extractVideoData(link, shortCode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public JsonNode getUniversalData(String webpage) {
        Matcher m = UNIVERSAL_DATA_RE.matcher(webpage);
        if (!m.find()) {
            return MissingNode.getInstance();
        }
        JsonNode root;
        try {
            root = mapper.readTree(m.group(1));
        } catch (Exception e) {
            return MissingNode.getInstance();
        }
        return root.path("__DEFAULT_SCOPE__");
    }
    public String reconstructLink(String userId, String type, String shortCode) {
        return "https://www.tiktok.com/@" + (userId == null ? "_" : userId) + "/" + type + "/" + shortCode;
    }
    public AbstractMediaItem extractVideoData(String url, String videoId) throws IOException {
        WebpageResult result = requestWebPage(url);
        JsonNode universalData = getUniversalData(result.content());

        JsonNode videoDetail = universalData.path("webapp.video-detail");

        int status = videoDetail.path("statusCode").asInt(0);
        if (status != 0) {
            log.warn("TikTok returned non-zero statusCode {} for {}", status, videoId);
            return null;
        }

        JsonNode itemStruct = videoDetail.path("itemInfo").path("itemStruct");
        if (!itemStruct.isObject()) {
            log.warn("TikTok itemStruct missing/empty for {}", videoId);
            return null;
        }

        if (itemStruct.has("imagePost")) {
            List<String> imageUrls = new ArrayList<>();
            for (JsonNode img : itemStruct.path("imagePost").path("images")) {
                String imageUrl = img.path("imageURL").path("urlList").path(0).asString();
                if (!imageUrl.isBlank()) {
                    imageUrls.add(imageUrl);
                }
            }
            if (imageUrls.isEmpty()) {
                log.warn("TikTok imagePost had no usable image URLs for {}", videoId);
                return null;
            }

            String audioUrl = null;
            JsonNode music = itemStruct.path("music");
            if (!music.path("playUrl").isMissingNode()) {
                String candidate = music.path("playUrl").asString();
                if (!candidate.isBlank()) {
                    audioUrl = candidate;
                }
            }
            return new TikTokSlideshowItem(imageUrls, audioUrl);
        } else {
            boolean hasVideo = itemStruct.path("video").isObject();
            boolean isClassified = itemStruct.path("isContentClassified").asBoolean(false);
            if (!hasVideo && isClassified) {
                log.warn("Content is age/sensitivity-restricted and requires login: {}", videoId);
                return null;
            }

            String videoUrl = extractBestWebFormat(itemStruct);
            return new TikTokVideoItem(videoId, videoUrl, result.cookie());
        }
    }
    /**
     * Extract a playable muxed video URL from a TikTok aweme_detail node.
     * Ported from yt-dlp's _extract_web_formats, trimmed to what a "pick a playable URL" resolver needs.
     * Returns the best candidate URL, or null if none usable.
     */
    public String extractBestWebFormat(JsonNode awemeDetail) {
        JsonNode videoInfo = awemeDetail.path("video");
        if (!videoInfo.isObject()) return null;

        // Ordered candidates, best-first. We collect URLs and pick the first playable one.
        List<String> candidates = new ArrayList<>();

        // 1. bitrateInfo[].PlayAddr.UrlList — the main quality ladder (each has mirror URLs)
        JsonNode bitrateInfo = videoInfo.path("bitrateInfo");
        if (bitrateInfo.isArray()) {
            for (JsonNode entry : bitrateInfo) {
                JsonNode playAddr = entry.path("PlayAddr");

                // skip bytevc2 (H.266/VVC) — unplayable by AVPlayer/ExoPlayer
                String urlKey = playAddr.path("UrlKey").asText("");
                if (urlKey.contains("bytevc2")) {
                    continue;
                }

                for (JsonNode u : playAddr.path("UrlList")) {
                    String url = u.asText(null);
                    if (url == null) continue;
                    url = protoRelative(url);
                    // skip media-video-hvc1 — may be video-only or 404
                    if (url.contains("/media-video-hvc1/")) continue;
                    candidates.add(url);
                }
            }
        }

        // 2. playAddr — fallback single "play address" (string, or array of {src})
        JsonNode playAddr = videoInfo.path("playAddr");
        if (playAddr.isTextual()) {
            candidates.add(protoRelative(playAddr.asText()));
        } else if (playAddr.isArray()) {
            for (JsonNode p : playAddr) {
                JsonNode src = p.path("src");
                if (src.isTextual()) candidates.add(protoRelative(src.asText()));
                else if (p.isTextual()) candidates.add(protoRelative(p.asText()));
            }
        }

        // NOTE: deliberately NOT collecting downloadAddr — it's the watermarked version (preference -2).
        // NOTE: deliberately NOT collecting music.playUrl — that's the separate audio track, not the video.

        // Filter out any URL pointing back at www.tiktok.com (broken placeholder formats)
        for (String url : candidates) {
            if (!"www.tiktok.com".equalsIgnoreCase(hostOf(url))) {
                return url;   // first usable candidate; bitrateInfo entries come first = higher quality
            }
        }
        return null;
    }

    /** Instagram/TikTok both serve protocol-relative URLs (//host/...) sometimes; force https. */
    public static String protoRelative(String url) {
        if (url == null) return null;
        if (url.startsWith("//")) return "https:" + url;
        return url;
    }

    public static String hostOf(String url) {
        try {
            return java.net.URI.create(url).getHost();
        } catch (Exception e) {
            return null;
        }
    }
}