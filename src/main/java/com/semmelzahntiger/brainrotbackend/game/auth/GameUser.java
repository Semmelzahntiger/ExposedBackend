package com.semmelzahntiger.brainrotbackend.game.auth;

import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.game.UserConnection;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class GameUser {
    public enum UserState {
        UNAUTHENTICATED,
        AUTHENTICATED
    }
    @Getter
    private @NotNull UserState userState = UserState.UNAUTHENTICATED;

    private boolean authenticated = false;
    @Getter
    private @Nullable UUID userUUID;
    @Getter
    private @Nullable String username;
    @Getter
    private @Nullable String email;
    @Getter
    private List<String> authorities = new ArrayList<>();
    @Getter
    private final UserConnection userConnection;

    public GameUser(UserConnection userConnection) {
        this.userConnection = userConnection;
    }
    public String getSessionId() {
        return userConnection.getSessionId();
    }

    public boolean authenticateAs(AppUser appUser) {
        if(authenticated) {
            log.warn("Tried to authenticate already authenticated user.");
            return false;
        }
        authenticated = true;
        userState = UserState.AUTHENTICATED;
        this.userUUID = appUser.getUserId();
        this.username = appUser.getUsername();
        this.email = appUser.getEmail();
        authorities.addAll(appUser.getAuthorities());
        return true;
    }
}
