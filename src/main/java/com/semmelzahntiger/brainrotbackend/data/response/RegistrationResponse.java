package com.semmelzahntiger.brainrotbackend.data.response;

public record RegistrationResponse(boolean success, String authToken, String refreshToken, String reason) {
}
