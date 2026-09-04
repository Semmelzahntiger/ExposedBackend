package com.semmelzahntiger.brainrotbackend.service.util;


import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem;
import com.semmelzahntiger.brainrotbackend.util.Constants;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ResolverService {
    private final Map<String, CdnResolver> resolvers;

    public ResolverService(Map<String, CdnResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public @Nullable AbstractMediaItem resolveContents(String url) {
        String resolvedPlatform = resolvePlatform(url);
        CdnResolver resolver = resolvers.get(resolvedPlatform + "_resolver");
        if(resolver == null) {
            log.warn("Link could not be resolved. No fitting Resolver found");
            return null;
        }
        try {
            return resolver.getCdnContents(url);
        } catch (Exception e) {
            log.error("Couldn't resolve Link", e);
            return null;
        }
    }

    private String resolvePlatform(String url) {
        if(url.contains("www.tiktok.com")) {
            return Constants.TIKTOK;
        }
        else if(url.contains("www.instagram.com")) {
            return Constants.INSTAGRAM;
        }
        else {
            return "";
        }
    }
}
