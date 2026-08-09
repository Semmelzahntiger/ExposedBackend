package com.semmelzahntiger.brainrotbackend.data.json;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;

public record LoginResponse(boolean success, @Nullable String authToken, @Nullable String refreshToken, @Nullable String error) {
}
