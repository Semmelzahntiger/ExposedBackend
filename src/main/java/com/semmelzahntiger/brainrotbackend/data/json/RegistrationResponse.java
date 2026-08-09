package com.semmelzahntiger.brainrotbackend.data.json;

public record RegistrationResponse(boolean success, String authToken, String refreshToken, String reason) {
}
