package com.semmelzahntiger.brainrotbackend.service;

import lombok.Getter;

import java.time.LocalDate;

public interface SocialMediaResource {
    @Getter
    enum ResourceType {
        LIKED("liked"),
        SAVED("saved"),
        REPOSTS("reposted"),
        COMMENTS("commented"),
        SEARCH("searched");
        private final String name;
        ResourceType(String name) {
            this.name = name;
        }
    }
    SocialMediaPlatform getPlatform();
    ResourceType getResourceType();
    LocalDate getTimestamp();
    String getResource();

}
