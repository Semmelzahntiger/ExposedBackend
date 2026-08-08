package com.semmelzahntiger.brainrotbackend.service;

import com.semmelzahntiger.brainrotbackend.data.AppUser;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


// Testing Version
@Service
public class UserRepository {
    public final Map<UUID, AppUser> users = new HashMap<>();

    private final PasswordService passwordService;
    public UserRepository(PasswordService passwordService) {
        this.passwordService = passwordService;
    }


    public Optional<AppUser> getUser(UUID uuid) {
        return Optional.ofNullable(users.get(uuid));
    }
    public Optional<AppUser> getUserByEmail(String email) {
        return users.values().stream().filter(user -> user.getEmail().equals(email)).findFirst();
    }
    public boolean canRegisterUser(String email) {
        for (AppUser value : users.values()) {
            if (value.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }
    public boolean registerUser(AppUser user) {
        if(canRegisterUser(user.getEmail())) {
            users.put(user.getUserId(), user);
            return true;
        }
        else {
            return false;
        }
    }

    @PostConstruct
    public void post() {
        String encrypted = passwordService.encode("testpassword");
        AppUser testUser = new AppUser(UUID.randomUUID(), "test", "test@test.com", encrypted);
        registerUser(testUser);
    }

}
