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
    public static int countOccurrences(String haystack, String needle) {
        int count = 0, idx = 0;
        while ((idx = haystack.indexOf(needle, idx)) != -1) {
            count++;
            idx += needle.length();
        }
        return count;
    }
}
