package com.semmelzahntiger.brainrotbackend.util;
public enum UserRoles {
    USER("USER"),
    ADMIN("ADMIN");
    private final String name;
    UserRoles(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
