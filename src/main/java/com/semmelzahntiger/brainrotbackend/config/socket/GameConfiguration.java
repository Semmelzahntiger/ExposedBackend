package com.semmelzahntiger.brainrotbackend.config.socket;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.socket.GameSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class GameConfiguration implements WebSocketConfigurer {
    private final GameManager gameManager;

    public GameConfiguration(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GameSocketHandler(gameManager), "/game");
    }
}
