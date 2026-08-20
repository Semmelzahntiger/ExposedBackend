package com.semmelzahntiger.brainrotbackend;

import com.semmelzahntiger.brainrotbackend.service.FileDecoderService;
import com.semmelzahntiger.brainrotbackend.service.InstagramParser;
import com.semmelzahntiger.brainrotbackend.service.SocialMediaResource;
import com.semmelzahntiger.brainrotbackend.util.exceptions.InvalidDataStructureException;
import org.apache.juli.LogUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
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
            List<SocialMediaResource> results = instagramParser.parseInstagramData(contents);
            System.out.println(results.size());

        } catch (IOException | InvalidDataStructureException exception) {
            failed = true;
            System.out.println(exception.getMessage());
        }
        Assertions.assertFalse(failed);
    }
}
