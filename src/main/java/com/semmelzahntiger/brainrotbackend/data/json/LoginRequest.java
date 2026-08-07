package com.semmelzahntiger.brainrotbackend.data;

import lombok.Getter;
import lombok.Setter;

public record LoginRequest(String email, String password) {

}
