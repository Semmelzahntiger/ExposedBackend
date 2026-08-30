package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.DenyRequestMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.StartGameMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartGameMessageListener implements GameMessageListener<StartGameMessage> {
    @Override
    public Class<StartGameMessage> getMessageClass() {
        return StartGameMessage.class;
    }

    @Override
    public void handleMessage(StartGameMessage message, GameUser user, GameManager manager) {
        if(user.inRoom()) {
            user.getCurrentRoom().startGame(user);
        }
        else {
            user.getConnection().sendMessage(new DenyRequestMessage());
        }
    }
}
