package com.semmelzahntiger.brainrotbackend;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.service.util.EnvironmentService;
import com.semmelzahntiger.brainrotbackend.service.auth.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JWTServiceTest {

    /**
     * The HMAC signing secret configured in the project's .env file (secret=...).
     * EnvironmentService is mocked so the test never touches the filesystem; using the
     * real value keeps the token round-trips faithful to production signing.
     */
    private static final String SECRET =
            "8affe13e27fea1f580d42306f17f7b00b8fbc97c52935a28ed02dc958ab71874";

    @Mock
    private EnvironmentService environmentService;

    @InjectMocks
    private JWTService jwtService;

    private UUID userId;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        when(environmentService.getSecret()).thenReturn(SECRET);
        userId = UUID.randomUUID();
        appUser = new AppUser(userId, "brainrot_enjoyer", "skibidi@example.com", List.of("USER"));
    }

    // ---------------------------------------------------------------------
    // createJWTToken
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("createJWTToken produces a token the service accepts as valid")
    void createJWTToken_producesVerifiableToken() {
        String token = jwtService.createJWTToken(appUser);

        assertNotNull(token);
        assertTrue(jwtService.validJWTToken(token));
    }

    @Test
    @DisplayName("createJWTToken embeds the subject, email, username and authority claims")
    void createJWTToken_containsExpectedClaims() {
        String token = jwtService.createJWTToken(appUser);

        DecodedJWT decoded = jwtService.getDecodedJWT(token).orElseThrow();
        assertEquals(userId.toString(), decoded.getSubject());
        assertEquals("skibidi@example.com", decoded.getClaim("email").asString());
        assertEquals("brainrot_enjoyer", decoded.getClaim("username").asString());
        assertEquals(List.of("ROLE_USER"), decoded.getClaim("authorities").asList(String.class));
    }

    @Test
    @DisplayName("createJWTToken includes every authority granted to the user")
    void createJWTToken_containsAllAuthorities() {
        appUser.addAuthority("ROLE_ADMIN");

        String token = jwtService.createJWTToken(appUser);

        DecodedJWT decoded = jwtService.getDecodedJWT(token).orElseThrow();
        assertEquals(List.of("ROLE_USER", "ROLE_ADMIN"),
                decoded.getClaim("authorities").asList(String.class));
    }

    @Test
    @DisplayName("createJWTToken sets an expiry roughly 15 minutes after issuing")
    void createJWTToken_setsFifteenMinuteExpiry() {
        String token = jwtService.createJWTToken(appUser);

        DecodedJWT decoded = jwtService.getDecodedJWT(token).orElseThrow();
        assertNotNull(decoded.getIssuedAt());
        assertNotNull(decoded.getExpiresAt());
        long minutes = ChronoUnit.MINUTES.between(
                decoded.getIssuedAt().toInstant(), decoded.getExpiresAt().toInstant());
        assertEquals(15, minutes);
        assertTrue(decoded.getExpiresAt().toInstant().isAfter(Instant.now()));
    }

    // ---------------------------------------------------------------------
    // getDecodedJWT / validJWTToken : valid tokens
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("getDecodedJWT returns a present Optional for a genuine token")
    void getDecodedJWT_validToken_returnsPresent() {
        String token = jwtService.createJWTToken(appUser);

        Optional<DecodedJWT> decoded = jwtService.getDecodedJWT(token);
        assertTrue(decoded.isPresent());
    }

    @Test
    @DisplayName("validJWTToken returns true for a genuine token")
    void validJWTToken_validToken_returnsTrue() {
        String token = jwtService.createJWTToken(appUser);

        assertTrue(jwtService.validJWTToken(token));
    }

    // ---------------------------------------------------------------------
    // getDecodedJWT / validJWTToken : invalid tokens
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("a token signed with a different secret is rejected")
    void getDecodedJWT_wrongSignature_returnsEmpty() {
        String foreignToken = JWT.create()
                .withSubject(userId.toString())
                .withExpiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256("a-totally-different-signing-secret"));

        assertTrue(jwtService.getDecodedJWT(foreignToken).isEmpty());
        assertFalse(jwtService.validJWTToken(foreignToken));
    }

    @Test
    @DisplayName("an expired token is rejected even when signed with the correct secret")
    void getDecodedJWT_expiredToken_returnsEmpty() {
        String expiredToken = JWT.create()
                .withSubject(userId.toString())
                .withIssuedAt(Instant.now().minus(30, ChronoUnit.MINUTES))
                .withExpiresAt(Instant.now().minus(15, ChronoUnit.MINUTES))
                .sign(Algorithm.HMAC256(SECRET));

        assertTrue(jwtService.getDecodedJWT(expiredToken).isEmpty());
        assertFalse(jwtService.validJWTToken(expiredToken));
    }

    @Test
    @DisplayName("a token whose payload was tampered with is rejected")
    void getDecodedJWT_tamperedPayload_returnsEmpty() {
        String token = jwtService.createJWTToken(appUser);
        String[] parts = token.split("\\.");
        // Corrupt the payload segment so it no longer matches the signature.
        String tampered = parts[0] + "." + parts[1] + "x." + parts[2];

        assertTrue(jwtService.getDecodedJWT(tampered).isEmpty());
        assertFalse(jwtService.validJWTToken(tampered));
    }

    @Test
    @DisplayName("a structurally malformed string is rejected")
    void getDecodedJWT_malformedToken_returnsEmpty() {
        assertTrue(jwtService.getDecodedJWT("this-is-not-a-jwt").isEmpty());
        assertFalse(jwtService.validJWTToken("this-is-not-a-jwt"));
    }

    @Test
    @DisplayName("an empty token string is rejected")
    void getDecodedJWT_emptyToken_returnsEmpty() {
        assertTrue(jwtService.getDecodedJWT("").isEmpty());
        assertFalse(jwtService.validJWTToken(""));
    }

    @Test
    @DisplayName("a null token is rejected without throwing")
    void getDecodedJWT_nullToken_returnsEmpty() {
        assertTrue(jwtService.getDecodedJWT(null).isEmpty());
        assertFalse(jwtService.validJWTToken(null));
    }
}
