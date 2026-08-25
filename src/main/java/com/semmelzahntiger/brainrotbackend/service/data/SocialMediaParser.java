package com.semmelzahntiger.brainrotbackend.service.data;

import com.semmelzahntiger.brainrotbackend.util.exceptions.MalformedDataStructureException;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SocialMediaParser {

    @NotNull
    List<SocialMediaResource> parseData(Map<String, byte[]> data) throws MalformedDataStructureException;

    SocialMediaPlatform getPlatform();

    record LinkResource(SocialMediaPlatform platform, SocialMediaResource.ResourceType type, LocalDate timeStamp, String url) implements SocialMediaResource {
        @Override
        public SocialMediaPlatform getPlatform() {
            return platform;
        }

        @Override
        public ResourceType getResourceType() {
            return type;
        }

        @Override
        public LocalDate getTimestamp() {
            return timeStamp;
        }

        @Override
        public String getResource() {
            return url;
        }
    }
    record TextResource(SocialMediaPlatform platform, SocialMediaResource.ResourceType type, LocalDate timeStamp, String text) implements SocialMediaResource {

        @Override
        public SocialMediaPlatform getPlatform() {
            return platform;
        }

        @Override
        public ResourceType getResourceType() {
            return type;
        }

        @Override
        public LocalDate getTimestamp() {
            return timeStamp;
        }

        @Override
        public String getResource() {
            return text;
        }
    }
}
