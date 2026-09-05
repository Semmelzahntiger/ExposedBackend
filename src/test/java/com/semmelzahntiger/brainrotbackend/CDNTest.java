package com.semmelzahntiger.brainrotbackend;

import com.semmelzahntiger.brainrotbackend.service.util.InstagramResolver;
import com.semmelzahntiger.brainrotbackend.service.util.TikTokResolver;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.InstagramMediaItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.TikTokSlideshowItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.TikTokVideoItem;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class CDNTest {
    private static TikTokResolver tikTokResolver;
    private static InstagramResolver instagramResolver;

    @BeforeAll
    public static void setup() {
        tikTokResolver = new TikTokResolver();
        instagramResolver = new InstagramResolver();
    }

    @Test
    public void testCarousel() throws Exception {
        InstagramResolver instagramResolver = new InstagramResolver();
        AbstractMediaItem item = instagramResolver.getCdnContents("https://www.instagram.com/p/DcailhOnG8v");
        log.info(item.toString());
        assertNotNull(item);
        InstagramMediaItem instagramMediaItem = (InstagramMediaItem) item;
        for (InstagramMediaItem.InstagramCDNEntry entry : instagramMediaItem.getEntries()) {
            log.info(entry.url());
        }

    }
    @Test
    public void testRegularPost() throws Exception {
        String link = "https://www.instagram.com/p/Dcg0DnoPD6N/";
        InstagramResolver instagramResolver = new InstagramResolver();
        AbstractMediaItem item = instagramResolver.getCdnContents(link);
        log.info(item.toString());
        assertNotNull(item);
        InstagramMediaItem instagramMediaItem = (InstagramMediaItem) item;
        for (InstagramMediaItem.InstagramCDNEntry entry : instagramMediaItem.getEntries()) {
            log.info(entry.url());
        }
    }
    @Test
    public void testReelWithStillImage() throws Exception {
        String link = "https://www.instagram.com/reels/DcyhPwmgno0/";
        InstagramResolver instagramResolver = new InstagramResolver();
        AbstractMediaItem item = instagramResolver.getCdnContents(link);
        assertNotNull(item);
        InstagramMediaItem instagramMediaItem = ((InstagramMediaItem) item);
        for (InstagramMediaItem.InstagramCDNEntry entry : instagramMediaItem.getEntries()) {
            log.info(entry.url());
        }
    }
    @Test
    public void testReel() throws Exception {
        InstagramResolver instagramResolver = new InstagramResolver();
        AbstractMediaItem item = instagramResolver.getCdnContents("https://www.instagram.com/reel/DbR210tu5lP/");
        assertNotNull(item);
        InstagramMediaItem instagramMediaItem = ((InstagramMediaItem) item);
        for (InstagramMediaItem.InstagramCDNEntry entry : instagramMediaItem.getEntries()) {
            log.info(entry.url());
        }
    }
    @Test
    public void testActualTikTok() throws Exception {
        TikTokResolver tikTokResolver = new TikTokResolver();
        AbstractMediaItem item = tikTokResolver.getCdnContents("https://www.tiktok.com/@hoodinformatik/video/7680888932157967638");
        assertNotNull(item);
        TikTokVideoItem tiktokVideoItem = ((TikTokVideoItem) item);
        log.info(tiktokVideoItem.getCdnUrl());
        log.info(tiktokVideoItem.getTtChainToken());
    }
    @Test
    public void testActualSlideshow() throws Exception {
        TikTokResolver tikTokResolver = new TikTokResolver();
        AbstractMediaItem item = tikTokResolver.getCdnContents("https://www.tiktok.com/@dragonverse.89/photo/7681241395478203680");
        assertNotNull(item);
        TikTokSlideshowItem slideshowItem = ((TikTokSlideshowItem) item);
        for (String imageUrl : slideshowItem.getImageUrls()) {
            log.info("Image Url: {}", imageUrl);
        }
        log.info("Audio Url {}",slideshowItem.getAudioUrl());
    }
    @Test
    public void testMultiTikTok() throws Exception {
        TikTokResolver tikTokResolver = new TikTokResolver();
        String[] urls = new String[] {
                "https://www.tiktok.com/@hoodinformatik/video/7680888932157967638",
                "https://www.tiktok.com/@dragonverse.89/photo/7681241395478203680",
                "https://www.tiktok.com/@bl33d.xvi/video/7681123528367754509"
        };
        log.info("Size: {}", urls.length);
        List<AbstractMediaItem> mediaItems = new ArrayList<>();
        for (String url : urls) {
            AbstractMediaItem item = tikTokResolver.getCdnContents(url);
            assertNotNull(item);

            if(item instanceof TikTokVideoItem videoItem) {
                log.info("------- VIDEO ITEM OUTPUT -------");
                log.info(videoItem.getCdnUrl());
                log.info(videoItem.getTtChainToken());
                log.info("------- END OF VIDEO ITEM OUTPUT -------");
            }
            else if(item instanceof TikTokSlideshowItem slideshowItem) {
                log.info("------- SLIDE SHOW ITEM OUTPUT -------");
                log.info("------- IMAGES -------");
                for (String imageUrl : slideshowItem.getImageUrls()) {
                    log.info(imageUrl);
                }
                log.info("------- AUDIO -------");
                log.info(slideshowItem.getAudioUrl());
                log.info("------- END OF SLIDE SHOW ITEM OUTPUT -------");
            }
            else {
               continue;
            }
            mediaItems.add(item);
        }
        log.info("Size: {}", mediaItems.size());
    }
    @Test
    public void testTikTokVideoAccess() throws Exception {
        AbstractMediaItem item = tikTokResolver.getCdnContents("https://www.tiktok.com/@hoodinformatik/video/7680888932157967638");
        TikTokVideoItem tikTokVideoItem = ((TikTokVideoItem) item);

        String chainCookie = tikTokVideoItem.getTtChainToken();
        String cdnUrl = tikTokVideoItem.getCdnUrl();

        assertNotNull(cdnUrl, "resolver returned no CDN url");
        assertNotNull(chainCookie, "resolver returned no tt_chain_token");

        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .build();

        Request request = new Request.Builder()
                .url(cdnUrl)
                .header("Referer", "https://www.tiktok.com/")
                .header("Cookie", "tt_chain_token=" + chainCookie)
                .build();

        try (Response response = client.newCall(request).execute()) {
            int code = response.code();
            System.out.println("status      = " + code);
            System.out.println("content-type= " + response.header("Content-Type"));
            System.out.println("content-len = " + response.header("Content-Length"));

            byte[] head = new byte[1024];
            int read = response.body().byteStream().read(head);
            System.out.println("first bytes read = " + read);

            assertTrue(code == 200 || code == 206, "expected 200/206 but got " + code);
            assertTrue(read > 0, "status was OK but no body bytes came through");
        }
    }
}
