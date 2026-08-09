package com.semmelzahntiger.brainrotbackend.data;

import java.util.UUID;

public record UserPrincipal(UUID userUuid, String email, String username, String[] authorities) {

}
