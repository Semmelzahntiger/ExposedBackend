package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.service.PasswordService;
import com.semmelzahntiger.brainrotbackend.service.RefreshTokenService;
import com.semmelzahntiger.brainrotbackend.service.UserRepository;
import com.semmelzahntiger.brainrotbackend.data.json.LoginRequest;
import com.semmelzahntiger.brainrotbackend.data.json.LoginResponse;
import com.semmelzahntiger.brainrotbackend.data.json.RegistrationRequest;
import com.semmelzahntiger.brainrotbackend.data.json.RegistrationResponse;
import com.semmelzahntiger.brainrotbackend.service.JWTService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class Authentication {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordService passwordService;
    private final RefreshTokenService refreshTokenService;

    public Authentication(UserRepository dataAccess,
                          JWTService jwtService,
                          PasswordService passwordService,
                          RefreshTokenService refreshTokenService) {
        this.userRepository = dataAccess;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.email();
        String password = loginRequest.password();
        Optional<AppUser> user = userRepository.getUserByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(new LoginResponse(false, null, null, "User not found."));
        }
        AppUser appUser = user.get();
        boolean correctPassword = passwordService.matches(password, appUser.getPassword());
        if (!correctPassword) {
            return ResponseEntity
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(new LoginResponse(false, null, null, "Incorrect password."));
        }
        String jwtToken = jwtService.createJWTToken(appUser);
        String refreshToken = refreshTokenService.getNewRefreshToken();
        return ResponseEntity.ok(new LoginResponse(true, jwtToken, refreshToken, null));
    }

    @PostMapping("/register")
    public  ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        String email = registrationRequest.email();
        String username = registrationRequest.username();
        String password = registrationRequest.password();
    }
}
