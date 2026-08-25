package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.ChangeRoleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChangeRoleMessageListener implements GameMessageListener<ChangeRoleMessage> {
    @Override
    public Class<ChangeRoleMessage> getMessageClass() {
        return ChangeRoleMessage.class;
    }

    @Override
    public void handleMessage(ChangeRoleMessage message, GameUser user, GameManager manager) {

    }
}
