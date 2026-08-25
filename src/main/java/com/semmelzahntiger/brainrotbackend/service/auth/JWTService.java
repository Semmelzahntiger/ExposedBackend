package com.semmelzahntiger.brainrotbackend.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.data.entities.UserEntity;
import com.semmelzahntiger.brainrotbackend.service.util.EnvironmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class JWTService {
    private final EnvironmentService environmentService;
    public JWTService (EnvironmentService envService) {
        environmentService = envService;
    }
    @Deprecated
    public String createJWTToken(AppUser appUser) {
        return JWT.create()
                .withSubject(String.valueOf(appUser.getUserId()))
                .withClaim("email", appUser.getEmail())
                .withClaim("username", appUser.getUsername())
                .withArrayClaim("authorities", appUser.getAuthorities().toArray(new String[0]))
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(environmentService.getSecret()));
    }
    public String createJWTToken(UserEntity user) {
        return JWT.create()
                .withSubject(String.valueOf(user.getUserId()))
                .withClaim("email", user.getEmail())
                .withClaim("username", user.getUsername())
                .withArrayClaim("authorities", user.getAuthorities().toArray(new String[0]))
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(environmentService.getSecret()));
    }

    public Optional<DecodedJWT> getDecodedJWT(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(environmentService.getSecret());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return Optional.of(jwt);
        }
        catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
    public boolean validJWTToken(String token) {
        return getDecodedJWT(token).isPresent();
    }
    public Optional<AppUser> getJWTDataFromToken(String token) {
        Optional<DecodedJWT> decodedOptional = getDecodedJWT(token);
        if(decodedOptional.isPresent()) {
            DecodedJWT decoded = decodedOptional.get();
            UUID uuid = UUID.fromString(decoded.getSubject());
            String email = String.valueOf(decoded.getClaim("email"));
            String username = String.valueOf(decoded.getClaim("username"));
            List<String> authorities = List.of(decoded.getClaim("authorities").asArray(String.class));
            return Optional.of(new AppUser(uuid, email, username, authorities));
        }
        log.info("JWT INVALID");
        return Optional.empty();
    }

    public record JWTData(UUID userId, String email,String username, List<String> authorities) {}
}
