package com.semmelzahntiger.brainrotbackend.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppUser {
    @Getter
    private final UUID userId;
    @Getter
    private String username;
    @Getter
    private String password;
    @Getter
    private String email;
    private List<String> authorities = new ArrayList<>();
    public AppUser(UUID userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public void addAuthority(String authority) {
        authorities.add(authority);
    }
    public List<String> getAuthorities() {
        return List.of(authorities.toArray(new String[0]));
    }
    public void changeUsername(String username) {
        this.username = username;
    }
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }
    public void changeEmail(String email) {
        this.email = email;
    }
}
