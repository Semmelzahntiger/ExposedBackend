package com.semmelzahntiger.brainrotbackend.data.json;

public record LoginResponse(boolean success, String authToken, String refreshToken ) {
}
