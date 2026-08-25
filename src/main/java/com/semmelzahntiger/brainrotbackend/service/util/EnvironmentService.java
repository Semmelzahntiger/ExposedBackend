package com.semmelzahntiger.brainrotbackend.service.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
public class EnvironmentService {

    @Value("${app.jwt.secret}")
    private String secret;

    public EnvironmentService() {}

}
