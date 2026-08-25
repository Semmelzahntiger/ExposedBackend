package com.semmelzahntiger.brainrotbackend.game.auth;

import org.springframework.web.socket.WebSocketSession;

public record ConnectionIdentifier(String socketId, WebSocketSession session) {
    public static ConnectionIdentifier of(WebSocketSession session) {
        return new ConnectionIdentifier(session.getId(), session);
    }
}
