package com.semmelzahntiger.brainrotbackend.util;

import lombok.Getter;
import tools.jackson.databind.ObjectMapper;

public class MapperUtil {
    @Getter
    public static final ObjectMapper MESSAGE_MAPPER = new ObjectMapper();
}
