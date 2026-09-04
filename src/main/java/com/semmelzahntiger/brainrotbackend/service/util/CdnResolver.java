package com.semmelzahntiger.brainrotbackend.service.util;

import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem;

public interface CdnResolver {

    AbstractMediaItem getCdnContents(String url) throws Exception;
    record WebpageResult(String content, String finalUrl, String cookie) {
        public WebpageResult(String content, String finalUrl) {
            this(content, finalUrl, null);
        }
    }


}
