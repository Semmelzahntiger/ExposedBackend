package com.semmelzahntiger.brainrotbackend.service.util.cdn;

import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import lombok.Getter;

@Getter
public class TikTokVideoItem extends AbstractMediaItem {
    protected final String url;
    protected final String headerUrl;
    protected final String ttChainCookie;

    public TikTokVideoItem(String url, String headerUrl, String ttChainCookie) {
        super(SocialMediaPlatform.TIKTOK, MediaType.VIDEO, "tiktok_video_media");
        this.url = url;
        this.headerUrl = headerUrl;
        this.ttChainCookie = ttChainCookie;
    }
}
