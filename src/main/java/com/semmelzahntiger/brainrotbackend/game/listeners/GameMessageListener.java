package com.semmelzahntiger.brainrotbackend.game;

import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;

public interface GameMessageListener<T extends InboundNetworkMessage> {
    Class<T> getMessageClass();
    void handle(T message, GameUser user, GameManager manager);
}
