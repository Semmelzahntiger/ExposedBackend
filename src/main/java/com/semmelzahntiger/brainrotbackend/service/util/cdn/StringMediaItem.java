package com.semmelzahntiger.brainrotbackend.service.util.cdn;

import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import lombok.Getter;

@Getter
public class StringMediaItem extends AbstractMediaItem {
    private final String stringMedia;
    public StringMediaItem(SocialMediaPlatform platform, String stringMedia) {
        super(platform, MediaType.TEXT, "string_media_item");
        this.stringMedia = stringMedia;
    }
}
