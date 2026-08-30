package com.semmelzahntiger.brainrotbackend.game.auth;

import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.game.UserConnection;
import com.semmelzahntiger.brainrotbackend.game.room.Room;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Slf4j
public class GameUser {
    public enum UserAuthState {
        UNAUTHENTICATED,
        AUTHENTICATED
    }
    private @NotNull GameUser.UserAuthState userState = UserAuthState.UNAUTHENTICATED;

    private boolean authenticated = false;
    private @Nullable UUID userUUID = null;
    private @Nullable String username = null;
    private @Nullable String email = null;
    private final List<String> authorities = new ArrayList<>();
    private final UserConnection connection;
    private @Nullable String currentRoomCode = null;
    private @Nullable Room currentRoom = null;


    public GameUser(UserConnection userConnection) {
        this.connection = userConnection;
    }
    public String getSessionId() {
        return connection.getSessionId();
    }

    public void authenticateAs(AppUser appUser) {
        authenticated = true;
        userState = UserAuthState.AUTHENTICATED;
        this.userUUID = appUser.getUserId();
        this.username = appUser.getUsername();
        this.email = appUser.getEmail();
        authorities.addAll(appUser.getAuthorities());
    }
    public synchronized boolean setRoom(@Nullable Room room) {
        if(room == null) {
            currentRoom = null;
            currentRoomCode = null;
            return true;
        }
        if(currentRoom != null) {
            log.warn("User already in Room.");
            return false;
        }
        currentRoom = room;
        currentRoomCode = room.getRoomCode();
        return true;
    }
    public boolean inRoom() {
        return currentRoom != null;
    }
}
