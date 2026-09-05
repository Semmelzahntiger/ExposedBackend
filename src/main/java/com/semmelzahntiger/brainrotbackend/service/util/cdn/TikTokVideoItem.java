package com.semmelzahntiger.brainrotbackend.service.util.cdn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


@Getter
public class TikTokVideoItem extends AbstractMediaItem {
    @JsonIgnore
    protected final String postId;
    @JsonIgnore
    protected final String cdnUrl;
    @JsonIgnore
    protected final String ttChainToken;

    @Setter
    protected String roomId;

    public TikTokVideoItem(String postId, String cdnUrl, String ttChainToken) {
        super(SocialMediaPlatform.TIKTOK, MediaType.VIDEO, "tiktok_video_media");
        this.postId = postId;
        this.cdnUrl = cdnUrl;
        this.ttChainToken = ttChainToken;
    }
}
