package com.semmelzahntiger.brainrotbackend.data;

public record RegistrationResponse(boolean success, String authToken, String refreshToken) {
}
