package com.semmelzahntiger.brainrotbackend.data;

public record LoginResponse(boolean success, String authToken, String refreshToken ) {
}
