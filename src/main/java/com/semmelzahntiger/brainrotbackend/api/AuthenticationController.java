package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.data.entities.RefreshTokenEntity;
import com.semmelzahntiger.brainrotbackend.data.entities.UserEntity;
import com.semmelzahntiger.brainrotbackend.data.repositories.RefreshTokenRepository;
import com.semmelzahntiger.brainrotbackend.data.repositories.UserRepository;
import com.semmelzahntiger.brainrotbackend.data.json.requests.RefreshRequest;
import com.semmelzahntiger.brainrotbackend.data.json.requests.ValidationRequest;
import com.semmelzahntiger.brainrotbackend.service.util.PasswordService;
import com.semmelzahntiger.brainrotbackend.service.auth.RefreshTokenService;
import com.semmelzahntiger.brainrotbackend.data.json.requests.LoginRequest;
import com.semmelzahntiger.brainrotbackend.data.json.response.LoginResponse;
import com.semmelzahntiger.brainrotbackend.data.json.requests.RegistrationRequest;
import com.semmelzahntiger.brainrotbackend.data.json.response.RegistrationResponse;
import com.semmelzahntiger.brainrotbackend.service.auth.JWTService;
import com.semmelzahntiger.brainrotbackend.util.UserRoles;
import com.semmelzahntiger.brainrotbackend.util.ValidatorUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
@Slf4j
public class AuthenticationController {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTService jwtService;
    private final PasswordService passwordService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationController(UserRepository userRepository,
                                    RefreshTokenRepository refreshTokenRepository,
                                    JWTService jwtService,
                                    PasswordService passwordService,
                                    RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(HttpServletRequest servletRequest, @RequestBody LoginRequest loginRequest) {
        log.info("New Login Request from '{}'", servletRequest.getRemoteAddr());
        String email = loginRequest.email();
        String password = loginRequest.password();
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(new LoginResponse(false, null, null, "User not found."));
        }
        UserEntity userEntity = user.get();
        boolean correctPassword = passwordService.matches(password, userEntity.getPassword());
        if (!correctPassword) {
            log.debug("Login Request passed incorrect password");
            return ResponseEntity
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(new LoginResponse(false, null, null, "Incorrect password."));
        }
        AppUser appUser = AppUser.fromUserEntity(userEntity);
        String jwtToken = jwtService.createJWTToken(appUser);

        String refreshToken = refreshTokenService.getNewRefreshToken();
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.getNewRefreshTokenEntity(userEntity, refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
        log.info("User '{}' logged in successfully.", userEntity.getEmail());
        return ResponseEntity.ok(new LoginResponse(true, jwtToken, refreshToken, null));
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponse> register(HttpServletRequest request,@RequestBody RegistrationRequest registrationRequest) {
        log.info("New Registration Request from '{}'", request.getRemoteAddr());
        String email = registrationRequest.email();
        if(!ValidatorUtil.validEmail(email)) {
            log.debug("Registration with invalid E-Mail");
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(new RegistrationResponse(false, null, null, "Email is invalid."));
        }

        String username = registrationRequest.username();
        if(username.length() > 15) {
            log.debug("Registration with too long name");
            return ResponseEntity.status(HttpServletResponse.SC_UNPROCESSABLE_CONTENT).body(new RegistrationResponse(false, null, null, "Username is too long."));
        }
        if(!ValidatorUtil.validUsername(username)) {
            log.debug("Registration with invalid username");
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(new RegistrationResponse(false, null, null, "Username is invalid."));
        }

        String password = registrationRequest.password();
        if(password.length() < 8) {
            log.debug("Registration with too short password");
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(new RegistrationResponse(false, null, null, "Password must be at least 8 characters."));
        }

        boolean userExists = userRepository.existsByEmail(email);
        if(userExists) {
            log.debug("Registrator tried registering with an occupied E-Mail");
            return ResponseEntity.status(HttpServletResponse.SC_CONFLICT).body(new RegistrationResponse(false, null, null, "User with the same email already exists."));
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(passwordService.encode(password));
        userEntity.setUsername(username);
        userEntity.setAuthorities(List.of(UserRoles.USER.getName()));
        userRepository.save(userEntity);

        AppUser appUser = AppUser.fromUserEntity(userEntity);

        String jwt = jwtService.createJWTToken(appUser);
        String refreshToken = refreshTokenService.getNewRefreshToken();

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.getNewRefreshTokenEntity(userEntity, refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
        log.info("User '{}' registered successfully", registrationRequest.email());
        return ResponseEntity.ok().body(new RegistrationResponse(true, jwt, refreshToken, null));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verify(HttpServletRequest request,@RequestBody ValidationRequest validationRequest) {
        String token = validationRequest.token();
        log.debug("JWT Verification Request from '{}'", request.getRemoteAddr());
        return jwtService.validJWTToken(token) ? ResponseEntity.ok().build() : ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
    }
    @PostMapping("/get-access-token")
    public ResponseEntity<Map<String, String>> refresh(HttpServletRequest request, @RequestBody RefreshRequest refreshRequest) {
        log.info("New Access Token Request from '{}'", request.getRemoteAddr());
        String refreshToken = refreshRequest.refreshToken();
        Optional<RefreshTokenEntity> tokenOptional = refreshTokenService.getRefreshTokenEntity(refreshToken);
        if(tokenOptional.isPresent()) {
            RefreshTokenEntity refreshTokenEntity = tokenOptional.get();
            UserEntity user = refreshTokenEntity.getOwnerEntity();
            String jwt = jwtService.createJWTToken(user);
            log.info("Returned fresh Access Token for '{}'", user.getEmail());
            return ResponseEntity.ok().body(Map.of("token",jwt));
        }
        log.info("Refresh Token invalid.");
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(Map.of("message", "Refresh Token Invalid"));
    }
}
