package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.CreateRoomMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateRoomMessageListener implements GameMessageListener<CreateRoomMessage> {
    @Override
    public Class<CreateRoomMessage> getMessageClass() {
        return CreateRoomMessage.class;
    }

    @Override
    public void handleMessage(CreateRoomMessage message, GameUser user, GameManager manager) {

    }
}
