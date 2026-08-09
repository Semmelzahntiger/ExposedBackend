package com.semmelzahntiger.brainrotbackend.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RefreshTokenService {

    public String getNewRefreshToken(UUID userUUID) {

    }

    public boolean validateRefreshToken(String refreshToken) {
        return false;
    }
}
