package com.semmelzahntiger.brainrotbackend.util;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.MissingNode;

public class JsonUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static JsonNode readByteArrayToJson(byte[] bytes) {
        return readByteArrayToJson(bytes, MAPPER);
    }
    public static JsonNode readByteArrayToJson(byte[] bytes, ObjectMapper mapper) {
        return mapper.readTree(bytes);
    }

    public static JsonNode traverseAny(JsonNode root, String[]... paths) {
        for (String[] path : paths) {
            JsonNode current = root;
            for (String key : path) {
                current = current.path(key);
            }
            if (!current.isMissingNode() && !current.isNull()) {
                return current;
            }
        }
        return MissingNode.getInstance();
    }
}
