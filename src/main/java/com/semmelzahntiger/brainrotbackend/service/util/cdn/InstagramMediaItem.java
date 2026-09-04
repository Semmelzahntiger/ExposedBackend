package com.semmelzahntiger.brainrotbackend.service.util.cdn;

import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import lombok.Getter;

import java.util.List;

public class InstagramMediaItem extends AbstractMediaItem {
    @Getter
    protected final List<InstagramCDNEntry> entries;

    public InstagramMediaItem(MediaType type,List<InstagramCDNEntry> entries) {
        super(SocialMediaPlatform.INSTAGRAM, type, "instagram_mixed_media");
        this.entries = entries;
    }
    public record InstagramCDNEntry(MediaType type, String url) {}

}
