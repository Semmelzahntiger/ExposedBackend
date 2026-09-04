package com.semmelzahntiger.brainrotbackend.service.util.cdn;

import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import lombok.Getter;

@Getter
public class AbstractMediaItem {
    public enum MediaType { VIDEO, IMAGE, MIXED}
    protected final SocialMediaPlatform platform;
    protected final MediaType type;
    protected final String distinctionType;
    public AbstractMediaItem(SocialMediaPlatform platform, MediaType type, String distinctionType) {
        this.platform = platform;
        this.type = type;
        this.distinctionType = distinctionType;
    }
}
