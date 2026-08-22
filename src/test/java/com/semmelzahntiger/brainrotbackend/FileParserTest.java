package com.semmelzahntiger.brainrotbackend;

import com.semmelzahntiger.brainrotbackend.service.FileDecoderService;
import com.semmelzahntiger.brainrotbackend.service.InstagramParser;
import com.semmelzahntiger.brainrotbackend.service.SocialMediaResource;
import com.semmelzahntiger.brainrotbackend.service.TikTokParser;
import com.semmelzahntiger.brainrotbackend.util.RandomUtil;
import com.semmelzahntiger.brainrotbackend.util.exceptions.MalformedDataStructureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class FileParserTest {

    public static FileDecoderService getFileDecoderService() {
        return new FileDecoderService();
    }
    public static InstagramParser getInstagramParser() {
        return new InstagramParser();
    }
    public static TikTokParser getTikTokParser() {
        return new TikTokParser();
    }


    @Test
    void testZipDecoding() {
        boolean failed  = false;
        try (InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("instagram_data.zip")) {
            FileDecoderService fileDecoderService = getFileDecoderService();
            Map<String, byte[]> contents = fileDecoderService.extract(fileStream);
            for (String s : contents.keySet()) {
                System.out.println("Detected file: " + s);
            }
        } catch (IOException exception) {
            failed = true;
            System.out.println(exception.getMessage());
        }
        Assertions.assertFalse(failed);
    }

    @Test
    void testInstagramParser() {
        boolean failed  = false;
        try (InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("instagram_data.zip")) {
            FileDecoderService fileDecoderService = getFileDecoderService();
            Map<String, byte[]> contents = fileDecoderService.extract(fileStream);

            InstagramParser instagramParser = getInstagramParser();
            List<SocialMediaResource> results = instagramParser.parseData(contents);
            int size = results.size();
            System.out.println(size);
            for (int i = 0; i < 20; i++) {
                int idx = RandomUtil.getRandomBetweenSize(size);
                System.out.println(results.get(idx).toString());
            }

        } catch (IOException | MalformedDataStructureException exception) {
            failed = true;
            System.out.println(exception.getMessage());
        }
        Assertions.assertFalse(failed);
    }
    @Test
    void testTikTokParser() {
        boolean failed  = false;
        try (InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("tiktok_data.zip")) {
            FileDecoderService fileDecoderService = getFileDecoderService();
            Map<String, byte[]> contents = fileDecoderService.extract(fileStream);

            TikTokParser tikTokParser = getTikTokParser();
            List<SocialMediaResource> results = tikTokParser.parseData(contents);
            int size = results.size();
            System.out.println(size);
            for (int i = 0; i < 5; i++) {
                int idx = RandomUtil.getRandomBetweenSize(size);
                System.out.println(results.get(idx).toString());
            }

        } catch (IOException | MalformedDataStructureException e) {
            failed = true;
            System.out.println(e.getMessage());
        }
        Assertions.assertFalse(failed);
    }
    @Test
    void testMalformedData() {
        boolean instagramFailed = false;
        boolean tikFailed = false;
        try (InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("data_malformed.zip")) {
            FileDecoderService fileDecoderService = getFileDecoderService();
            Map<String, byte[]> contents = fileDecoderService.extract(fileStream);

            InstagramParser instagramParser = getInstagramParser();
            List<SocialMediaResource> results = instagramParser.parseData(contents);
            int size = results.size();
            System.out.println(size);
            for (int i = 0; i < 5; i++) {
                int idx = RandomUtil.getRandomBetweenSize(size);
                System.out.println(results.get(idx).toString());
            }

        } catch (IOException | MalformedDataStructureException exception) {
            instagramFailed = true;
            System.out.println(exception.getMessage());
        }
        try (InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream("data_malformed.zip")) {
            FileDecoderService fileDecoderService = getFileDecoderService();
            Map<String, byte[]> contents = fileDecoderService.extract(fileStream);

            TikTokParser tikTokParser = getTikTokParser();
            List<SocialMediaResource> results = tikTokParser.parseData(contents);
            int size = results.size();
            System.out.println(size);
            for (int i = 0; i < 5; i++) {
                int idx = RandomUtil.getRandomBetweenSize(size);
                System.out.println(results.get(idx).toString());
            }
        } catch (IOException | MalformedDataStructureException e) {
            tikFailed = true;
            System.out.println(e.getMessage());
        }
        Assertions.assertTrue(instagramFailed);
        Assertions.assertTrue(tikFailed);
    }
}
