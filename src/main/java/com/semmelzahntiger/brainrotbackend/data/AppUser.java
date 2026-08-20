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
    private String password;
    @Getter
    private String email;
    private final List<String> authorities = new ArrayList<>();
    public AppUser(UUID userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        authorities.add("ROLE_USER");
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

    public static AppUser fromUserEntity(UserEntity userEntity) {
        return new AppUser(
                userEntity.getUserId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getPassword()
        );
    }
}
