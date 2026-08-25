package com.semmelzahntiger.brainrotbackend.service.data;

import com.semmelzahntiger.brainrotbackend.util.DateUtil;
import com.semmelzahntiger.brainrotbackend.util.JsonUtil;
import com.semmelzahntiger.brainrotbackend.util.exceptions.MalformedDataStructureException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("tiktok")
public class TikTokParser implements SocialMediaParser {
    public static final String TIKTOK_DATA = "user_data_tiktok.json";

    @Override
    public List<SocialMediaResource> parseData(Map<String, byte[]> data) throws MalformedDataStructureException {
        byte[] tiktokData = data.get(TIKTOK_DATA);
        if(tiktokData == null) {
            throw new MalformedDataStructureException("Unexpected structure in Tiktok input");
        }
        JsonNode dataJson = JsonUtil.readByteArrayToJson(tiktokData);
        return getResources(dataJson);
    }

    @Override
    public SocialMediaPlatform getPlatform() {
        return SocialMediaPlatform.TIKTOK;
    }

    private List<SocialMediaResource> getResources(JsonNode json) {
        List<SocialMediaResource> links = new ArrayList<>();
        JsonNode activity = json.path("Your Activity");
        JsonNode likes = activity.path("Like List").path("ItemFavoriteList");
        JsonNode searches = activity.path("Searches").path("SearchList");
        JsonNode favorites = activity.path("Favorite Videos").path("FavoriteVideoList");
        if(likes.isMissingNode() || searches.isMissingNode() || favorites.isMissingNode()) {
            throw new MalformedDataStructureException("Unexpected structure in Tiktok input");
        }
        for (JsonNode like : likes) {
            String date = like.get("date").asString(null);
            String href = like.get("link").asString(null);
            if(date != null && href != null) {
                LocalDate parsedDate = DateUtil.parseDate(date);
                links.add(new LinkResource(SocialMediaPlatform.TIKTOK, SocialMediaResource.ResourceType.LIKED, parsedDate, href));
            }
        }
        for (JsonNode favorite : favorites) {
            String date = favorite.get("Date").asString(null);
            String href = favorite.get("Link").asString(null);
            if(date != null && href != null) {
                LocalDate parsedDate = DateUtil.parseDate(date);
                links.add(new LinkResource(SocialMediaPlatform.TIKTOK, SocialMediaResource.ResourceType.SAVED, parsedDate, href));
            }
        }
        for (JsonNode search : searches) {
            String date = search.get("Date").asString(null);
            String text = search.get("SearchTerm").asString(null);
            if(date != null && text != null) {
                LocalDate parsedDate = DateUtil.parseDate(date);
                links.add(new TextResource(SocialMediaPlatform.TIKTOK, SocialMediaResource.ResourceType.SEARCH, parsedDate, text));
            }
        }
        return links;
    }
}
