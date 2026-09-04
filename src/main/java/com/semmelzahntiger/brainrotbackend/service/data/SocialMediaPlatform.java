package com.semmelzahntiger.brainrotbackend.service.data;

import com.fasterxml.jackson.annotation.JsonValue;
import com.semmelzahntiger.brainrotbackend.util.Constants;
import lombok.Getter;

@Getter
public enum SocialMediaPlatform {
    INSTAGRAM(Constants.INSTAGRAM),
    TIKTOK(Constants.TIKTOK);
    @JsonValue
    private final String name;
    SocialMediaPlatform(String name) {
        this.name = name;
    }
    public static SocialMediaPlatform getByPlatformName(String name) {
        for (SocialMediaPlatform value : values()) {
            if(value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
