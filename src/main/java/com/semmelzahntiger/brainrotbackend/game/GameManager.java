package com.semmelzahntiger.brainrotbackend.game;

import com.semmelzahntiger.brainrotbackend.game.auth.UserManager;
import com.semmelzahntiger.brainrotbackend.service.socket.SocketMessageHandler;
import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
public class GameManager {
    @Getter
    private final UserManager userManager;
    private final SocketMessageHandler messageHandler;
    private final GameMessageManager messageManager;

    public GameManager(SocketMessageHandler messageHandler, UserManager connectionManager, GameMessageManager manager) {
        this.messageHandler = messageHandler;
        this.userManager = connectionManager;
        this.messageManager = manager;
    }

    public void addNewIncomingConnection(WebSocketSession session) {
        userManager.handleNewUnauthorizedConnection(session);
    }
    public void connectionClosed(WebSocketSession session) {
        userManager.connectionClosed(session);
    }
    public void connectionClosed(UserConnection connection) {
        userManager.connectionClosed(connection);
    }

    public void onMessage(WebSocketSession session, TextMessage message) {
        InboundNetworkMessage inboundMessage = messageHandler.parseMessage(message);
        log.info("Received '{}' Message from '{}'", inboundMessage.getClass(), session.getRemoteAddress());
        messageManager.handleMessage(session, inboundMessage, this);
    }
}
