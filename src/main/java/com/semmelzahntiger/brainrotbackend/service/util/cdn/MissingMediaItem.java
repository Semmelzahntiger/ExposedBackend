package com.semmelzahntiger.brainrotbackend.service.util.cdn;

import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;

public class MissingMediaItem extends AbstractMediaItem {
    public MissingMediaItem(SocialMediaPlatform platform) {
        super(platform, MediaType.NONE, "missing_media");
    }
}
