package com.semmelzahntiger.brainrotbackend.service;

public interface SocialMediaResource {
    enum ResourceType {
        LIKED,
        SAVED,
        REPOSTS,
        COMMENTS,
    }
}
