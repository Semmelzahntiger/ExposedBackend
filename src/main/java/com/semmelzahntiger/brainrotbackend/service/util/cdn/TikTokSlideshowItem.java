package com.semmelzahntiger.brainrotbackend.service.util.cdn;

import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import lombok.Getter;

import java.util.List;

@Getter
public class TikTokSlideshowItem extends AbstractMediaItem {
    protected final List<String> imageUrls;
    protected final String audioUrl;
    public TikTokSlideshowItem(List<String> imageUrls, String audioUrl) {
        super(SocialMediaPlatform.TIKTOK, MediaType.IMAGE, "tiktok_slide_show_media");
        this.imageUrls = imageUrls;
        this.audioUrl = audioUrl;
    }
}
