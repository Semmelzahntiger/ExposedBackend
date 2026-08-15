package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.data.UserEntity;
import com.semmelzahntiger.brainrotbackend.data.UserRepository;
import com.semmelzahntiger.brainrotbackend.data.json.TestResponse;
import com.semmelzahntiger.brainrotbackend.service.PasswordService;
import com.semmelzahntiger.brainrotbackend.service.RefreshTokenService;
import com.semmelzahntiger.brainrotbackend.service.InMemoryRepository;
import com.semmelzahntiger.brainrotbackend.data.json.LoginRequest;
import com.semmelzahntiger.brainrotbackend.data.json.LoginResponse;
import com.semmelzahntiger.brainrotbackend.data.json.RegistrationRequest;
import com.semmelzahntiger.brainrotbackend.data.json.RegistrationResponse;
import com.semmelzahntiger.brainrotbackend.service.JWTService;
import com.semmelzahntiger.brainrotbackend.util.ValidatorUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class Authentication {

    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordService passwordService;
    private final RefreshTokenService refreshTokenService;

    public Authentication(InMemoryRepository dataAccess,
                          UserRepository userRepository,
                          JWTService jwtService,
                          PasswordService passwordService,
                          RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordService = passwordService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
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
            return ResponseEntity
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(new LoginResponse(false, null, null, "Incorrect password."));
        }
        AppUser appUser = AppUser.fromUserEntity(userEntity);
        String jwtToken = jwtService.createJWTToken(appUser);
        String refreshToken = refreshTokenService.getNewRefreshToken(appUser.getUserId());
        return ResponseEntity.ok(new LoginResponse(true, jwtToken, refreshToken, null));
    }

    @PostMapping("/register")
    public  ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest registrationRequest) {
        String email = registrationRequest.email();
        if(!ValidatorUtil.validEmail(email)) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(new RegistrationResponse(false, null, null, "Email is invalid."));
        }

        String username = registrationRequest.username();
        if(username.length() > 15) {
            return ResponseEntity.status(HttpServletResponse.SC_UNPROCESSABLE_CONTENT).body(new RegistrationResponse(false, null, null, "Username is too long."));
        }
        if(!ValidatorUtil.validUsername(username)) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(new RegistrationResponse(false, null, null, "Username is invalid."));
        }

        String password = registrationRequest.password();
        if(password.length() < 8) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body(new RegistrationResponse(false, null, null, "Password must be at least 8 characters."));
        }

        boolean userExists = userRepository.existsByEmail(email);
        if(userExists) {
            return ResponseEntity.status(HttpServletResponse.SC_CONFLICT).body(new RegistrationResponse(false, null, null, "User with the same email already exists."));
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(email);
        userEntity.setPassword(password);
        userEntity.setUsername(username);
        userRepository.save(userEntity);

        AppUser appUser = AppUser.fromUserEntity(userEntity);

        String jwt = jwtService.createJWTToken(appUser);
        String refresh = refreshTokenService.getNewRefreshToken(appUser.getUserId());

        return ResponseEntity.ok().body(new RegistrationResponse(true, jwt, refresh, null));
    }


}
