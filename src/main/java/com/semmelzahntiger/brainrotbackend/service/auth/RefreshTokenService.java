package com.semmelzahntiger.brainrotbackend.service;

import com.semmelzahntiger.brainrotbackend.data.entities.RefreshTokenEntity;
import com.semmelzahntiger.brainrotbackend.data.repositories.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String getNewRefreshToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return ENCODER.encodeToString(bytes);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken).isPresent();
    }

    public Optional<RefreshTokenEntity> getRefreshTokenEntity(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }
}
