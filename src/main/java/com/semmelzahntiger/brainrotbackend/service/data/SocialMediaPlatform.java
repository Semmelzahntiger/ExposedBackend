package com.semmelzahntiger.brainrotbackend.service.data;

import com.semmelzahntiger.brainrotbackend.util.Constants;
import lombok.Getter;

@Getter
public enum SocialMediaPlatform {
    INSTAGRAM(Constants.INSTAGRAM),
    TIKTOK(Constants.TIKTOK);
    private final String name;
    SocialMediaPlatform(String name) {
        this.name = name;
    }

}
