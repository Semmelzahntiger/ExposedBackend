package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.LeaveRoomMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LeaveRoomMessageListener implements GameMessageListener<LeaveRoomMessage> {
    @Override
    public Class<LeaveRoomMessage> getMessageClass() {
        return LeaveRoomMessage.class;
    }

    @Override
    public void handleMessage(LeaveRoomMessage message, GameUser user, GameManager manager) {

    }
}
