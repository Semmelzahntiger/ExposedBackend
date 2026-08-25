package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.AuthenticationMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmAuthenticationMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyAuthenticationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationMessageListener implements GameMessageListener<AuthenticationMessage> {
    @Override
    public Class<AuthenticationMessage> getMessageClass() {
        return AuthenticationMessage.class;
    }

    @Override
    public void handle(AuthenticationMessage message, GameUser user, GameManager manager) {
        handleMessage(message, user, manager);
    }

    @Override
    public void handleMessage(AuthenticationMessage message, GameUser user, GameManager manager) {
        boolean success = manager.getUserManager().authorizeConnection(user, message.token());
        if(success) {
            log.info("'{}' logged in successfully.", user.getUsername());
            user.getUserConnection().sendMessage(new ConfirmAuthenticationMessage());
        }
        else {
            log.info("'{}' failed to authenticate", user.getUserConnection().getRemoteAddress());
            user.getUserConnection().sendMessage(new DenyAuthenticationMessage());
        }
    }
}
