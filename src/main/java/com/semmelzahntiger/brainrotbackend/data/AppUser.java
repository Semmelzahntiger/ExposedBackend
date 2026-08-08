package com.semmelzahntiger.brainrotbackend.data;

import lombok.Getter;

import java.util.UUID;

public class AppUser {
    @Getter
    private final UUID userId;
    @Getter
    private final String username;
    @Getter
    private final String password;
    @Getter
    private String email;
    public AppUser(UUID userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
    }

}
