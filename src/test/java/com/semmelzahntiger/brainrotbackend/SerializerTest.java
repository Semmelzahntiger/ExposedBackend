package com.semmelzahntiger.brainrotbackend;

import com.semmelzahntiger.brainrotbackend.service.util.InstagramResolver;
import com.semmelzahntiger.brainrotbackend.service.util.TikTokResolver;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.TikTokVideoItem;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

@Slf4j
public class SerializerTest {

    private static InstagramResolver instagramResolver;
    private static TikTokResolver tikTokResolver;
    private static ObjectMapper mapper;

    @BeforeAll
    static void setup() {
        instagramResolver = new InstagramResolver();
        tikTokResolver = new TikTokResolver();
        mapper = new ObjectMapper();
    }

    @Test
    public void testInstagramMediaSerialization() throws Exception {
        String url = "https://www.instagram.com/reel/DbR210tu5lP/";
        AbstractMediaItem item = instagramResolver.getCdnContents(url);
        Assertions.assertNotNull(item);
        printJsonOutput(item);
    }
    @Test
    public void testInstagramCarouselSerialization() throws Exception {
        String url = "https://www.instagram.com/p/DcailhOnG8v";
        AbstractMediaItem item = instagramResolver.getCdnContents(url);
        Assertions.assertNotNull(item);
        printJsonOutput(item);
    }

    @Test
    public void testTikTokVideoSerialization() throws Exception {
        AbstractMediaItem item = tikTokResolver.getCdnContents("https://www.tiktok.com/@hoodinformatik/video/7680888932157967638");
        TikTokVideoItem videoItem = ((TikTokVideoItem) item);
        videoItem.setRoomId("XXXXXX");
        Assertions.assertNotNull(item);
        printJsonOutput(item);
    }
    @Test
    public void testTikTokSlideShowSerialization() throws Exception {
        AbstractMediaItem item = tikTokResolver.getCdnContents("https://www.tiktok.com/@dragonverse.89/photo/7681241395478203680");
        Assertions.assertNotNull(item);
        printJsonOutput(item);
    }
    public static void printJsonOutput(AbstractMediaItem mediaItem) {
        log.info("------- BASE INFORMATION -------");
        log.info("CLASS: {}", mediaItem.getClass().getSimpleName());
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mediaItem);
        log.info("------- JSON OUTPUT -------");
        log.info(json);
        log.info("------- END OF JSON OUTPUT -------");
    }
}
