package com.semmelzahntiger.brainrotbackend.data;

import com.semmelzahntiger.brainrotbackend.data.entities.UserEntity;
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
    private String email;
    private final List<String> authorities;
    public AppUser(UUID userId, String username, String email, List<String> authorities) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
    }
    public void addAuthority(String authority) {
        authorities.add(authority);
    }
    public List<String> getAuthorities() {
        return List.of(authorities.toArray(new String[0]));
    }

    public static AppUser fromUserEntity(UserEntity userEntity) {
        return new AppUser(
                userEntity.getUserId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getAuthorities()
        );
    }
}
