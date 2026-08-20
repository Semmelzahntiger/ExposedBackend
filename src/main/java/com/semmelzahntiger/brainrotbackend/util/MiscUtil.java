package com.semmelzahntiger.brainrotbackend.util;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

public class MiscUtil {

    public static Date readDateFromUnixTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        return Date.from(instant);
    }
}
