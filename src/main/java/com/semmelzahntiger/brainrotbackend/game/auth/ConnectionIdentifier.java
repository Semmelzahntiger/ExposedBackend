package com.semmelzahntiger.brainrotbackend.game;

import org.springframework.web.socket.WebSocketSession;

public record ConnectionIdentifier(String socketId, WebSocketSession session) {
}
