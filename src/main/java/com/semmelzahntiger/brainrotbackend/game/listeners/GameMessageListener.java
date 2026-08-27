package com.semmelzahntiger.brainrotbackend.game.listeners;

import com.semmelzahntiger.brainrotbackend.game.GameManager;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;

public interface GameMessageListener<T extends InboundNetworkMessage> {
    Class<T> getMessageClass();

    // Pre Authorization
    default void handle(T message, GameUser user, GameManager manager) {
        if(user.getUserState() == GameUser.UserAuthState.UNAUTHENTICATED) {
            return;
        }
        handleMessage(message, user, manager);
    }
    void handleMessage(T message, GameUser user, GameManager manager);
}
