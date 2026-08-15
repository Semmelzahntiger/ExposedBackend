package com.semmelzahntiger.brainrotbackend.util;

public class ValidatorUtil {
    public static boolean validUsername(String username) {
        return !username.isEmpty();
    }
    public static boolean validEmail(String email) {
        return true;
    }
}
