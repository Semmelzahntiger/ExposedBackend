package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.SubmitMultiGuessMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubmitMultiGuessMessageListener implements GameMessageListener<SubmitMultiGuessMessage> {
    @Override
    public Class<SubmitMultiGuessMessage> getMessageClass() {
        return SubmitMultiGuessMessage.class;
    }

    @Override
    public void handleMessage(SubmitMultiGuessMessage message, GameUser user, GameManager manager) {

    }
}
