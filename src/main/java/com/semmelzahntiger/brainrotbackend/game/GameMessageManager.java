package com.semmelzahntiger.brainrotbackend.game;

import com.semmelzahntiger.brainrotbackend.game.auth.UserManager;
import com.semmelzahntiger.brainrotbackend.game.listeners.GameMessageListener;
import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GameMessageManager {
    private final UserManager userManager;
    private final Map<Class<?>, List<GameMessageListener<?>>> messageListeners = new HashMap<>();

    public GameMessageManager(UserManager userManager,List<GameMessageListener<?>> listeners) {
        this.userManager = userManager;
        for (GameMessageListener<?> listener : listeners) {
            Class<?> messageClass = listener.getMessageClass();
            messageListeners.computeIfAbsent(messageClass,_ -> new ArrayList<>()).add(listener);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void handleMessage(WebSocketSession session, InboundNetworkMessage message, GameManager manager) {
        userManager.getUserBySessionId(session.getId()).ifPresent(user -> {
            List<GameMessageListener<?>> listeners = messageListeners.get(message.getClass());
            if(listeners != null) {
                for (GameMessageListener listener : listeners) {
                    listener.handle(message, user, manager);
                }
            }
        });
    }
}
