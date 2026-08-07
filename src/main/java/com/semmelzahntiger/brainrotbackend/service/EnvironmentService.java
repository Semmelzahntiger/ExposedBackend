package com.semmelzahntiger.brainrotbackend.service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentService {
    private final String secret;

    public EnvironmentService() {
        Dotenv dotenv = Dotenv.load();
        secret = dotenv.get("secret");
    }

    public String getSecret() {
        return secret;
    }
}
