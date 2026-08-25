package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.JoinRoomMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JoinRoomMessageListener implements GameMessageListener<JoinRoomMessage> {
    @Override
    public Class<JoinRoomMessage> getMessageClass() {
        return JoinRoomMessage.class;
    }

    @Override
    public void handleMessage(JoinRoomMessage message, GameUser user, GameManager manager) {

    }
}
