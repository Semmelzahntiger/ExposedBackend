package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.game.room.Room;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.JoinRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmJoinRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyJoinRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.RoomNotFoundMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class JoinRoomMessageListener implements GameMessageListener<JoinRoomMessage> {
    @Override
    public Class<JoinRoomMessage> getMessageClass() {
        return JoinRoomMessage.class;
    }

    @Override
    public void handleMessage(JoinRoomMessage message, GameUser user, GameManager manager) {
        if(!user.inRoom()) {
            Room room = manager.getRoomManager().findRoom(message.code());
            if(room == null) {
                user.getConnection().sendMessage(new RoomNotFoundMessage());
                return;
            }
            CompletableFuture<Boolean> joined = room.joinRoom(user);
            joined.thenAccept(success -> user.getConnection().sendMessage(success ? new ConfirmJoinRoomMessage() : new DenyJoinRoomMessage("Room is full.")));
        }
        else {
            user.getConnection().sendMessage(new DenyJoinRoomMessage("User is already in another Room."));
        }
    }
}
