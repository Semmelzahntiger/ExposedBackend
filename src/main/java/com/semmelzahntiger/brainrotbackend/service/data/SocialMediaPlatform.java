package com.semmelzahntiger.brainrotbackend.service.data;

import lombok.Getter;

@Getter
public enum SocialMediaPlatform {
    INSTAGRAM("instagram"),
    TIKTOK("tiktok");
    private final String name;
    SocialMediaPlatform(String name) {
        this.name = name;
    }
}
