package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.ReturnToLobbyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReturnToLobbyMessageListener implements GameMessageListener<ReturnToLobbyMessage> {
    @Override
    public Class<ReturnToLobbyMessage> getMessageClass() {
        return ReturnToLobbyMessage.class;
    }

    @Override
    public void handleMessage(ReturnToLobbyMessage message, GameUser user, GameManager manager) {

    }
}
