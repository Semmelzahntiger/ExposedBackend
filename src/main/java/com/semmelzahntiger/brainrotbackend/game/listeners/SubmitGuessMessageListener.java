package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.SubmitGuessMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubmitGuessMessageListener implements GameMessageListener<SubmitGuessMessage> {
    @Override
    public Class<SubmitGuessMessage> getMessageClass() {
        return SubmitGuessMessage.class;
    }

    @Override
    public void handleMessage(SubmitGuessMessage message, GameUser user, GameManager manager) {

    }
}
