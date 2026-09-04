package com.semmelzahntiger.brainrotbackend.service.util.cdn;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import lombok.Getter;

@Getter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "distinctionType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InstagramMediaItem.class, name = "instagram_mixed_media"),
        @JsonSubTypes.Type(value = TikTokSlideshowItem.class, name = "tiktok_slide_show_media"),
        @JsonSubTypes.Type(value = TikTokVideoItem.class, name = "tiktok_video_media"),
        @JsonSubTypes.Type(value = StringMediaItem.class, name = "string_media_item"),
        @JsonSubTypes.Type(value = MissingMediaItem.class, name = "missing_media")
})
public class AbstractMediaItem {
    public enum MediaType { VIDEO, IMAGE, MIXED, TEXT, NONE}
    protected final SocialMediaPlatform platform;
    protected final MediaType type;
    protected final String distinctionType;
    public AbstractMediaItem(SocialMediaPlatform platform, MediaType type, String distinctionType) {
        this.platform = platform;
        this.type = type;
        this.distinctionType = distinctionType;
    }
}
