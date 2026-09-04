package com.semmelzahntiger.brainrotbackend.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
// Credits go to yt-dlp
public class BlockBusterHeaderUtil {
    public static Map<String, String> generateBlockbusterHeaders() {
        return generateBlockbusterHeaders(2, 8);
    }

    public static Map<String, String> generateBlockbusterHeaders(int minHeaders, int maxHeaders) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int count = r.nextInt(minHeaders, maxHeaders + 1);
        Map<String, String> headers = new HashMap<>();
        for (int i = 0; i < count; i++) {
            String name  = randomLetters(8, 24);
            String value = randomLetters(16, 32);
            headers.put(name, value);
        }
        return headers;
    }

    public static final char[] CONSONANTS = "bcdfghjklmnpqrstvwxz".toCharArray();

    public static String randomLetters(int min, int max) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int len = r.nextInt(min, max + 1);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CONSONANTS[r.nextInt(CONSONANTS.length)]);
        }
        return sb.toString();
    }
}

