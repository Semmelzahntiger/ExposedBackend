package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.DenyRequestMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.SkipRoundMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SkipRoundMessageListener implements GameMessageListener<SkipRoundMessage> {
    @Override
    public Class<SkipRoundMessage> getMessageClass() {
        return SkipRoundMessage.class;
    }

    @Override
    public void handleMessage(SkipRoundMessage message, GameUser user, GameManager manager) {
        if(user.inRoom()) {
            user.getCurrentRoom().skipRound(user);
        }
        else {
            user.getConnection().sendMessage(new DenyRequestMessage());
        }
    }
}
