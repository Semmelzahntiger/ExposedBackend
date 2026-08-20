package com.semmelzahntiger.brainrotbackend.util;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static JsonNode readByteArrayToJson(byte[] bytes) {
        return readByteArrayToJson(bytes, MAPPER);
    }
    public static JsonNode readByteArrayToJson(byte[] bytes, ObjectMapper mapper) {
        return mapper.readTree(bytes);
    }
}
