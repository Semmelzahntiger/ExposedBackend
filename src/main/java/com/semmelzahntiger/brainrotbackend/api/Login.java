package com.semmelzahntiger.brainrotbackend.api;

import com.semmelzahntiger.brainrotbackend.service.DataBaseAccess;
import com.semmelzahntiger.brainrotbackend.data.json.LoginRequest;
import com.semmelzahntiger.brainrotbackend.data.json.LoginResponse;
import com.semmelzahntiger.brainrotbackend.data.json.RegistrationRequest;
import com.semmelzahntiger.brainrotbackend.data.json.RegistrationResponse;
import com.semmelzahntiger.brainrotbackend.service.JWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Login {

    private DataBaseAccess dataAccess;
    private JWTService jwtService;

    public Login(DataBaseAccess dataAccess, JWTService jwtService) {
        this.dataAccess = dataAccess;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.email();
        String password = loginRequest.password();
    }

    @PostMapping("/register")
    public  ResponseEntity<RegistrationResponse> register(@RequestBody RegistrationRequest registrationRequest) {

    }
}
