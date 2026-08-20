package com.semmelzahntiger.brainrotbackend.service;

import lombok.Getter;

import java.util.Date;

public interface SocialMediaParser {


    record LinkResource(SocialMediaPlatform platform, SocialMediaResource.ResourceType type, Date timeStamp, String url) implements SocialMediaResource {

    }
    record TextResource(SocialMediaPlatform platform, SocialMediaResource.ResourceType type, Date timeStamp, String text) implements SocialMediaResource {

    }
}
