package com.semmelzahntiger.brainrotbackend.service;

import com.semmelzahntiger.brainrotbackend.util.JsonUtil;
import com.semmelzahntiger.brainrotbackend.util.MiscUtil;
import com.semmelzahntiger.brainrotbackend.util.exceptions.MalformedDataStructureException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class InstagramParser implements SocialMediaParser {
    public static final String LIKED_POST_PATH = "your_instagram_activity/likes/liked_posts.json";
    public static final String SAVED_POST_PATH = "your_instagram_activity/saved/saved_posts.json";
    public static final String REPOSTS_PATH = "your_instagram_activity/media/reposts.json";



    public List<SocialMediaResource> parseData(Map<String, byte[]> data) {
        List<SocialMediaResource> resources = new ArrayList<>();
        byte[] likedPosts = data.get(LIKED_POST_PATH);
        byte[] savedPosts = data.get(SAVED_POST_PATH);
        byte[] repostedPosts = data.get(REPOSTS_PATH);
        if(likedPosts == null || savedPosts == null || repostedPosts == null) {
            throw new MalformedDataStructureException("Unexpected structure in Instagram input");
        }
        JsonNode likedPostsJson = JsonUtil.readByteArrayToJson(likedPosts);
        JsonNode savedPostsJson = JsonUtil.readByteArrayToJson(savedPosts);
        JsonNode repostedPostsJson = JsonUtil.readByteArrayToJson(repostedPosts);
        resources.addAll(getLinksFromPostsByType(likedPostsJson, SocialMediaResource.ResourceType.LIKED));
        resources.addAll(getLinksFromPostsByType(savedPostsJson, SocialMediaResource.ResourceType.SAVED));
        resources.addAll(getLinksFromPostsByType(repostedPostsJson, SocialMediaResource.ResourceType.REPOSTS));
        return resources;
    }

    private List<SocialMediaResource> getLinksFromPostsByType(JsonNode posts, SocialMediaResource.ResourceType type) {
        List<SocialMediaResource> result = new ArrayList<>();
        for (JsonNode likedPost : posts) {
            JsonNode timestampNode = likedPost.get("timestamp");
            if(timestampNode.isMissingNode()) {
                continue;
            }
            long timestamp = timestampNode.asLong();
            LocalDate date = MiscUtil.readDateFromUnixTimestamp(timestamp);
            JsonNode href = likedPost.path("label_values").path(0).path("href");
            if(href.isMissingNode()) {
                continue;
            }
            String url = href.stringValue(null);
            if(url != null) {
                result.add(new LinkResource(SocialMediaPlatform.INSTAGRAM, type, date, url));
            }
        }
        return result;
    }
}
