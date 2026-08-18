package com.semmelzahntiger.brainrotbackend.data.response;

import jakarta.annotation.Nullable;

public record LoginResponse(boolean success, @Nullable String authToken, @Nullable String refreshToken, @Nullable String error) {
}
