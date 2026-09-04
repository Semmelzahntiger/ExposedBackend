package com.semmelzahntiger.brainrotbackend;

import com.semmelzahntiger.brainrotbackend.service.util.InstagramResolver;
import com.semmelzahntiger.brainrotbackend.service.util.TikTokResolver;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.InstagramMediaItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.TikTokSlideshowItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.TikTokVideoItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CDNTest {

    @Test
    public void testCarousel() throws Exception {
        InstagramResolver instagramResolver = new InstagramResolver();
        AbstractMediaItem item = instagramResolver.getCdnContents("https://www.instagram.com/p/DcailhOnG8v");
        log.info(item.toString());
        Assertions.assertNotNull(item);
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
        Assertions.assertNotNull(item);
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
        Assertions.assertNotNull(item);
        InstagramMediaItem instagramMediaItem = ((InstagramMediaItem) item);
        for (InstagramMediaItem.InstagramCDNEntry entry : instagramMediaItem.getEntries()) {
            log.info(entry.url());
        }
    }
    @Test
    public void testReel() throws Exception {
        InstagramResolver instagramResolver = new InstagramResolver();
        AbstractMediaItem item = instagramResolver.getCdnContents("https://www.instagram.com/reel/DbR210tu5lP/");
        Assertions.assertNotNull(item);
        InstagramMediaItem instagramMediaItem = ((InstagramMediaItem) item);
        for (InstagramMediaItem.InstagramCDNEntry entry : instagramMediaItem.getEntries()) {
            log.info(entry.url());
        }
    }
    @Test
    public void testActualTikTok() throws Exception {
        TikTokResolver tikTokResolver = new TikTokResolver();
        AbstractMediaItem item = tikTokResolver.getCdnContents("https://www.tiktok.com/@hoodinformatik/video/7680888932157967638");
        Assertions.assertNotNull(item);
        TikTokVideoItem tiktokVideoItem = ((TikTokVideoItem) item);
        log.info(tiktokVideoItem.getUrl());
        log.info(tiktokVideoItem.getTtChainCookie());
    }
    @Test
    public void testActualSlideshow() throws Exception {
        TikTokResolver tikTokResolver = new TikTokResolver();
        AbstractMediaItem item = tikTokResolver.getCdnContents("https://www.tiktok.com/@dragonverse.89/photo/7681241395478203680");
        Assertions.assertNotNull(item);
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
            Assertions.assertNotNull(item);

            if(item instanceof TikTokVideoItem videoItem) {
                log.info("------- VIDEO ITEM OUTPUT -------");
                log.info(videoItem.getUrl());
                log.info(videoItem.getTtChainCookie());
                log.info(videoItem.getHeaderUrl());
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
    public static int countOccurrences(String haystack, String needle) {
        int count = 0, idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) != -1) {
            count++;
            idx += needle.length();
        }
        return count;
    }
}
