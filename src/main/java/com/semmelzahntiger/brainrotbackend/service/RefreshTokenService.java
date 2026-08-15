package com.semmelzahntiger.brainrotbackend.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    public String getNewRefreshToken(UUID userUUID) {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return ENCODER.encodeToString(bytes);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return false;
    }
}
