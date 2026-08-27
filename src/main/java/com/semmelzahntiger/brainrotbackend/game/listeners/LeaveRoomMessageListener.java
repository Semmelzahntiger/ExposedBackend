package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.DenyRequestMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.LeaveRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmLeaveRoomMessage;
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
        if(user.inRoom()) {
            user.getCurrentRoom().leaveRoom(user);
            user.getConnection().sendMessage(new ConfirmLeaveRoomMessage());
        }
        else {
            user.getConnection().sendMessage(new DenyRequestMessage());
        }
    }
}
