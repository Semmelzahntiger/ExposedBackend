package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.CreateRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmCreateRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyCreateRoomMessage;
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
        if(user.inRoom()) {
            manager.getRoomManager().createRoom(user);
            user.getConnection().sendMessage(new ConfirmCreateRoomMessage());
        }
        else {
            user.getConnection().sendMessage(new DenyCreateRoomMessage("User is already in a room."));
        }
    }
}
