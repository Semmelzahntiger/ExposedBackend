package com.semmelzahntiger.brainrotbackend.game;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserManager {
    private final Set<ConnectionIdentifier> unauthorizedConnections = new HashSet<>();

    public void handleNewUnauthorizedConnection(WebSocketSession session) {
        ConnectionIdentifier identifier = new ConnectionIdentifier(session.getId(), session);
        unauthorizedConnections.add(identifier);
    }
}
