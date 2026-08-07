package com.semmelzahntiger.brainrotbackend.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.hibernate.cfg.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class JWTService {
    private final EnvironmentService environmentService;
    public JWTService (EnvironmentService envService) {
        environmentService = envService;
    }

    public String createJWTToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withClaim("username", username)
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(environmentService.getSecret()));
    }

    public Optional<String> decodeUsernameFromJWT(String token){
        try {
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(environmentService.getSecret()))
                    .build().verify(token);
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }

    }

}
