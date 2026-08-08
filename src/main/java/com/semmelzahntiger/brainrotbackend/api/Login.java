package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.service.UserRepository;
import com.semmelzahntiger.brainrotbackend.data.json.LoginRequest;
import com.semmelzahntiger.brainrotbackend.data.json.LoginResponse;
import com.semmelzahntiger.brainrotbackend.data.json.RegistrationRequest;
import com.semmelzahntiger.brainrotbackend.data.json.RegistrationResponse;
import com.semmelzahntiger.brainrotbackend.service.JWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class Login {

    private UserRepository dataAccess;
    private JWTService jwtService;

    public Login(UserRepository dataAccess, JWTService jwtService) {
        this.dataAccess = dataAccess;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.email();
        String password = loginRequest.password();
    }

    @PostMapping("/register")
    public  ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest registrationRequest) {

    }
}
