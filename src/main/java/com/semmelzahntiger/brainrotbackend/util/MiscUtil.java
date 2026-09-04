package com.semmelzahntiger.brainrotbackend.util;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MiscUtil {
    public static final String ENCODING_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";

    public static LocalDate readDateFromUnixTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
    public static BigInteger decodeBaseN(String input, String table) {
        Map<Character, Integer> charToIndex = new HashMap<>();
        for (int i = 0; i < table.length(); i++) {
            charToIndex.put(table.charAt(i), i);
        }

        BigInteger base = BigInteger.valueOf(table.length());
        BigInteger result = BigInteger.ZERO;

        for (char c : input.toCharArray()) {
            Integer digit = charToIndex.get(c);
            if (digit == null) {
                throw new IllegalArgumentException("Character '" + c + "' not in table");
            }
            result = result.multiply(base).add(BigInteger.valueOf(digit));
        }

        return result;
    }
}
