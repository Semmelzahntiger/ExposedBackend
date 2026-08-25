package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.ChangeLobbyAdminMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChangeLobbyAdminMessageListener implements GameMessageListener<ChangeLobbyAdminMessage> {
    @Override
    public Class<ChangeLobbyAdminMessage> getMessageClass() {
        return ChangeLobbyAdminMessage.class;
    }

    @Override
    public void handleMessage(ChangeLobbyAdminMessage message, GameUser user, GameManager manager) {

    }
}
