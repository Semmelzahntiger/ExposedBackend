package com.semmelzahntiger.brainrotbackend.data;

import lombok.Getter;

public class AppUser {
    @Getter
    private final int userId;
    @Getter
    private final String username;
    @Getter
    private final String password;
    public AppUser(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

}
