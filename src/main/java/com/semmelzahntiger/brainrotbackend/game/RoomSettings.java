package com.semmelzahntiger.brainrotbackend.game;

import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaResource;
import com.semmelzahntiger.brainrotbackend.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class RoomSettings {
    protected int roomSize = 8;
    protected int rounds = 10;
    protected int roundTimeInSeconds = 30;
    protected List<String> enabledPlatforms = new ArrayList<>();
    protected List<String> enabledResources = new ArrayList<>();
    protected LocalDate beforeDate = LocalDate.ofInstant(Instant.MIN, ZoneId.systemDefault());


    public RoomSettings() {
        setDefaultPlatforms(enabledPlatforms);
        setResourceType(enabledResources);
    }

    protected void setDefaultPlatforms(List<String> enabledPlatforms) {
        enabledPlatforms.addAll(Arrays.stream(SocialMediaPlatform.values()).map(SocialMediaPlatform::getName).toList());
    }
    protected void setResourceType(List<String> resourceTypes) {
        resourceTypes.addAll(Arrays.stream(SocialMediaResource.ResourceType.values()).map(SocialMediaResource.ResourceType::getName).toList());
    }

}
