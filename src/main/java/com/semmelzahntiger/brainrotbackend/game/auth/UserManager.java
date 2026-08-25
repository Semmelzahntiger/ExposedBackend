package com.semmelzahntiger.brainrotbackend.game.auth;

import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.game.UserConnection;
import com.semmelzahntiger.brainrotbackend.service.auth.SocketAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class UserManager {
    private final SocketAuthenticationService socketAuthenticationService;
    private final Map<String, GameUser> users = new ConcurrentHashMap<>();
    private final Map<UUID, String> uuidSessionMap = new ConcurrentHashMap<>();
    private final Map<String, UUID> sessionUUIDMap = new ConcurrentHashMap<>();

    public UserManager(SocketAuthenticationService socketAuthenticationService) {
        this.socketAuthenticationService = socketAuthenticationService;
    }


    public void handleNewUnauthorizedConnection(WebSocketSession session) {
        String id = session.getId();
        if(users.containsKey(id)) {
            log.warn("Tried to handle unauthorized connection that's already registered");
            return;
        }
        UserConnection connection = new UserConnection(session);
        GameUser gameUser = new GameUser(connection);
        users.put(id, gameUser);
    }

    public Optional<GameUser> getUserBySessionId(String sessionId) {
        return Optional.of(users.get(sessionId));
    }
    public Optional<GameUser> getUserByUserUUID(UUID userUUID) {
        String sessionId = uuidSessionMap.get(userUUID);
        if(sessionId != null) {
            return Optional.of(users.get(sessionId));
        }
        return Optional.empty();
    }
    public Optional<UUID> getUUIDBySessionID(String session) {
        return Optional.ofNullable(sessionUUIDMap.get(session));
    }

    public boolean authorizeConnection(GameUser user, String token) {
        AppUser appUser = socketAuthenticationService.resolveUserByJWTToken(token);
        if(appUser != null) {
            log.info("Valid JWT");
            UUID uuid = appUser.getUserId();
            if(uuidSessionMap.containsKey(uuid)) {
                return false;
            }
            boolean success = user.authenticateAs(appUser);
            if(success) {
                uuidSessionMap.put(uuid, user.getSessionId());
                sessionUUIDMap.put(user.getSessionId(), uuid);
            }
            log.info("User '{}' logged into Socket",appUser.getEmail());
            return true;
        }
        else {
            return false;
        }
    }
    public void connectionClosed(WebSocketSession session) {
        connectionClosed(session.getId());
    }
    public void connectionClosed(UserConnection connection) {
        connectionClosed(connection.getSessionId());
    }
    public void connectionClosed(String sessionId) {
        GameUser gameUser = users.get(sessionId);
        log.info("'{}' disconnected. ({})", gameUser.getUserConnection().getRemoteAddress(), gameUser.getUserState().equals(GameUser.UserState.AUTHENTICATED) ?  gameUser.getUsername() : "Unauthenticated");
        // Todo: Cleanup

        //
        users.remove(sessionId);
        UUID userUUID = sessionUUIDMap.get(sessionId);
        sessionUUIDMap.remove(sessionId);
        if(userUUID != null) {
            uuidSessionMap.remove(userUUID);
        }
    }
}
