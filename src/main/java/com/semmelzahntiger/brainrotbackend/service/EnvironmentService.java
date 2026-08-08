package com.semmelzahntiger.brainrotbackend.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class EnvironmentService {
    private final String secret;

    public EnvironmentService() {
        Dotenv dotenv = Dotenv.load();
        secret = dotenv.get("secret");
    }

}
