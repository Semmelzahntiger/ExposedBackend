package com.semmelzahntiger.brainrotbackend.data.json.response;

public record RegistrationResponse(boolean success, String authToken, String refreshToken, String reason) {
}
