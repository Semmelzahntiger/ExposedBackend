package com.semmelzahntiger.brainrotbackend.service.util.cdn;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCookieJar implements CookieJar {
    private final Map<String, List<Cookie>> store = new ConcurrentHashMap<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        store.put(url.host(), cookies);   // page GET stores tt_chain_token here
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> out = new ArrayList<>();
        for (List<Cookie> list : store.values()) {
            for (Cookie c : list) {
                if (c.matches(url)) out.add(c);   // domain/path/secure check
            }
        }
        return out;
    }
}
