// Change this to match your project's test package.
package com.semmelzahntiger.brainrotbackend;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

/**
 * DIAGNOSTIC PROBE — not a pass/fail build test.
 *
 * Question it answers: does TikTok's MOBILE APP API return a populated
 * aweme_detail for a PHOTO post using yt-dlp's most optimistic config
 * (random device_id, NO iid, empty X-Argus, no real signature)?
 *
 * Reconstructs, 1:1, what yt-dlp's TikTokBaseIE does:
 *   _extract_aweme_app  -> POST /aweme/v1/multi/aweme/detail/
 *   _build_api_query    -> the big fabricated Android app-param query string
 *   _call_api_impl      -> app User-Agent + Accept + X-Argus:"" + odin_tt cookie
 *
 * Fill in AWEME_ID with the numeric id from one of YOUR known
 * tiktok.com/@user/photo/{id} URLs, then run it and read stdout.
 */
public class TikTokAppApiProbeTest {

    // ---- FILL THIS IN: numeric id from a tiktok.com/@user/photo/{id} URL ----
    private static final String AWEME_ID = "7663943554426850593";

    // yt-dlp _APP_INFO_DEFAULTS (musical_ly) + default api host.
    // If useast1a returns empty/blocked, try the alternates listed at the bottom.
    private static final String API_HOSTNAME = "api16-normal-c-useast1a.tiktokv.com";
    private static final String APP_NAME     = "musical_ly";
    private static final String APP_VERSION  = "35.1.3";
    private static final String MANIFEST_VER = "2023501030";
    private static final String AID          = "0";

    private static final Random RNG = new Random();

    @Test
    void probeAppApiForPhotoPost() throws Exception {
        if (AWEME_ID.startsWith("REPLACE")) {
            System.out.println("Set AWEME_ID to a real photo-post id first.");
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(Duration.ofSeconds(20))
                .build();

        String deviceId    = "7253472207308703259";
        String openudid    = randomHex(16);
        String odinTt      = randomHex(160);
        String cdid        = UUID.randomUUID().toString();
        long   nowSec      = System.currentTimeMillis() / 1000L;
        long   nowMs       = System.currentTimeMillis();
        String versionCode = versionCode(APP_VERSION); // "35.1.3" -> "350103"
        String userAgent   = "com.zhiliaoapp.musically/" + MANIFEST_VER
                + " (Linux; U; Android 13; en_US; Pixel 7; Build/TD1A.220804.031; Cronet/58.0.2991.0)";

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(API_HOSTNAME)
                .addPathSegments("aweme/v1/multi/aweme/detail/") // trailing slash matches yt-dlp
                // ---- _build_api_query (fabricated Android params, no signature) ----
                .addQueryParameter("device_platform", "android")
                .addQueryParameter("os", "android")
                .addQueryParameter("ssmix", "a")
                .addQueryParameter("_rticket", Long.toString(nowMs))
                .addQueryParameter("cdid", cdid)
                .addQueryParameter("channel", "googleplay")
                .addQueryParameter("aid", AID)
                .addQueryParameter("app_name", APP_NAME)
                .addQueryParameter("version_code", versionCode)
                .addQueryParameter("version_name", APP_VERSION)
                .addQueryParameter("manifest_version_code", MANIFEST_VER)
                .addQueryParameter("update_version_code", MANIFEST_VER)
                .addQueryParameter("ab_version", APP_VERSION)
                .addQueryParameter("resolution", "1080*2400")
                .addQueryParameter("dpi", "420")
                .addQueryParameter("device_type", "Pixel 7")
                .addQueryParameter("device_brand", "Google")
                .addQueryParameter("language", "en")
                .addQueryParameter("os_api", "29")
                .addQueryParameter("os_version", "13")
                .addQueryParameter("ac", "wifi")
                .addQueryParameter("is_pad", "0")
                .addQueryParameter("current_region", "US")
                .addQueryParameter("app_type", "normal")
                .addQueryParameter("sys_region", "US")
                .addQueryParameter("last_install_time",
                        Long.toString(nowSec - (86400 + RNG.nextInt(1123200 - 86400))))
                .addQueryParameter("timezone_name", "America/New_York")
                .addQueryParameter("residence", "US")
                .addQueryParameter("app_language", "en")
                .addQueryParameter("timezone_offset", "-14400")
                .addQueryParameter("host_abi", "armeabi-v7a")
                .addQueryParameter("locale", "en")
                .addQueryParameter("ac2", "wifi5g")
                .addQueryParameter("uoo", "1")
                .addQueryParameter("carrier_region", "US")
                .addQueryParameter("op_region", "US")
                .addQueryParameter("build_number", APP_VERSION)
                .addQueryParameter("region", "US")
                .addQueryParameter("ts", Long.toString(nowSec))
                .addQueryParameter("device_id", deviceId)
                .addQueryParameter("openudid", openudid)
                // iid deliberately OMITTED — this is the "no iid" optimistic case.
                .build();

        RequestBody body = new FormBody.Builder()
                .add("aweme_ids", "[" + AWEME_ID + "]")
                .add("request_source", "0")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json")
                .header("X-Argus", "")                 // yt-dlp sends this present-but-empty
                .header("Cookie", "odin_tt=" + odinTt) // set by yt-dlp on the API host
                .build();

        try (Response resp = client.newCall(request).execute()) {
            String respBody = resp.body() != null ? resp.body().string() : "";

            System.out.println("=== TikTok app-API probe ===");
            System.out.println("request host     : " + API_HOSTNAME);
            System.out.println("aweme_id         : " + AWEME_ID);
            System.out.println("device_id        : " + deviceId + "  (random, no iid)");
            System.out.println("HTTP status      : " + resp.code());
            System.out.println("body length      : " + respBody.length());
            System.out.println("looks like JSON  : " + respBody.stripLeading().startsWith("{"));

            // Structural markers. image_post_info / images / display_image / url_list
            // are the LIKELY photo fields — treat as hypotheses to verify, not gospel.
            for (String k : new String[]{
                    "\"aweme_details\"", "\"aweme_detail\"", "\"status_code\"", "\"status_msg\"",
                    "\"image_post_info\"", "\"images\"", "\"display_image\"", "\"url_list\"",
                    "\"video\"", "\"aweme_id\""}) {
                System.out.println("contains " + pad(k) + ": " + respBody.contains(k));
            }

            int statusIdx = respBody.indexOf("\"status_code\"");
            if (statusIdx >= 0) {
                System.out.println("status_code area : "
                        + respBody.substring(statusIdx, Math.min(respBody.length(), statusIdx + 80)));
            }

            System.out.println("---- body head (first 800 chars) ----");
            System.out.println(respBody.substring(0, Math.min(respBody.length(), 800)));
            System.out.println("=== end probe ===");
        }
    }

    /** "35.1.3" -> "350103" (each dotted part, 2-digit zero-padded, concatenated). */
    private static String versionCode(String version) {
        StringBuilder sb = new StringBuilder();
        for (String part : version.split("\\.")) {
            sb.append(String.format("%02d", Integer.parseInt(part)));
        }
        return sb.toString();
    }

    private static String randomHex(int len) {
        char[] hex = "0123456789abcdef".toCharArray();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(hex[RNG.nextInt(16)]);
        return sb.toString();
    }

    private static long randomLong(long origin, long bound) {
        return origin + (long) (RNG.nextDouble() * (bound - origin));
    }

    private static String pad(String s) {
        return s.length() >= 20 ? s : s + " ".repeat(20 - s.length());
    }

    // Alternate API hosts to try if useast1a returns an empty/blocked body:
    //   api22-normal-c-useast2a.tiktokv.com
    //   api19-normal-c-useast1a.tiktokv.com
    //   api16-normal-c-alisg.tiktokv.com   (Singapore)
    //   api16-normal-useast5.us.tiktokv.com
}