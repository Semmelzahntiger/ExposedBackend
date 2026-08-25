package com.semmelzahntiger.brainrotbackend.service.auth;


import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.data.repositories.UserRepository;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class SocketAuthenticationService {
    private final UserRepository userRepository;
    private final JWTService jwtService;

    public SocketAuthenticationService(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public @Nullable AppUser resolveUserByJWTToken(String token) {
        Optional<AppUser> dataOptional = jwtService.getJWTDataFromToken(token);
        if(dataOptional.isPresent()) {
            AppUser data = dataOptional.get();
            UUID uuid = data.getUserId();
            userRepository.existsByUserId(uuid);
            return data;
        }
        else {
            log.info("JWT invalid");
            return null;
        }

    }
}
