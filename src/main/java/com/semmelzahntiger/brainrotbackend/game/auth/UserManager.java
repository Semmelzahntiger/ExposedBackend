package com.semmelzahntiger.brainrotbackend.game.auth;

import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.game.UserConnection;
import com.semmelzahntiger.brainrotbackend.service.auth.SocketAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

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
        return Optional.ofNullable(users.get(sessionId));
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


    private final Object authLock = new Object();

    public boolean authenticateConnection(GameUser user, String token) {
        if (user.isAuthenticated()) {
            return false;
        }

        AppUser appUser = socketAuthenticationService.resolveUserByJWTToken(token);
        if (appUser == null) {
            return false;
        }

        UUID uuid = appUser.getUserId();
        String sessionId = user.getSessionId();

        synchronized (authLock) {
            if (uuidSessionMap.containsKey(uuid)) {
                String existingSession = uuidSessionMap.get(uuid);
                GameUser existingUser = users.get(existingSession);
                UUID existingUUID = sessionUUIDMap.get(existingSession);
                existingUser.getConnection().close(CloseStatus.NORMAL);
                uuidSessionMap.remove(existingUUID);

                sessionUUIDMap.remove(existingSession);
                uuidSessionMap.remove(existingUUID);
                users.remove(existingSession);
            }
            uuidSessionMap.put(uuid, sessionId);
            sessionUUIDMap.put(sessionId, uuid);
        }

        user.authenticateAs(appUser);

        log.info("User '{}' authenticated in Socket Connection", appUser.getEmail());
        return true;
    }
    public void connectionClosed(WebSocketSession session) {
        connectionClosed(session.getId());
    }
    public void connectionClosed(UserConnection connection) {
        connectionClosed(connection.getSessionId());
    }
    public void connectionClosed(String sessionId) {
        GameUser gameUser = users.get(sessionId);
        if(gameUser == null) {
            return;
        }
        log.info("'{}' disconnected. ({})", gameUser.getConnection().getRemoteAddress(), gameUser.getUserState().equals(GameUser.UserAuthState.AUTHENTICATED) ?  gameUser.getUsername() : "Unauthenticated");

        if(gameUser.inRoom()) {
            gameUser.getCurrentRoom().leaveRoom(gameUser);
        }

        users.remove(sessionId);
        UUID userUUID = sessionUUIDMap.get(sessionId);
        sessionUUIDMap.remove(sessionId);
        if(userUUID != null) {
            uuidSessionMap.remove(userUUID);
        }
    }
}
