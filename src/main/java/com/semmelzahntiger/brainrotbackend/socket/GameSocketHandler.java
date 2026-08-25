package com.semmelzahntiger.brainrotbackend.socket;


import com.semmelzahntiger.brainrotbackend.game.GameManager;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class GameSocketHandler extends TextWebSocketHandler {
    private final GameManager gameManager;

    public GameSocketHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("Socket session opened with '{}'.", session.getRemoteAddress());
        gameManager.addNewIncomingConnection(session);
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        gameManager.onMessage(session, message);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket Connection with '{}' closed. Reason: {}", session.getRemoteAddress(), status.getReason());
        gameManager.connectionClosed(session);
    }
}
