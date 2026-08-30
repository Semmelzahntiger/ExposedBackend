package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.DenyRequestMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.ChangeRoomSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChangeRoomSettingsListener implements GameMessageListener<ChangeRoomSettings> {
    @Override
    public Class<ChangeRoomSettings> getMessageClass() {
        return ChangeRoomSettings.class;
    }

    @Override
    public void handleMessage(ChangeRoomSettings message, GameUser user, GameManager manager) {
        if(user.inRoom()) {
            user.getCurrentRoom().changeRoomSettings(
                    user,
                    message.roomSize(),
                    message.rounds(),
                    message.roundTimeInSeconds(),
                    message.enabledPlatforms(),
                    message.enabledResources(),
                    message.beforeDate()
            );
        }
        else {
            user.getConnection().sendMessage(new DenyRequestMessage());
        }
    }
}
