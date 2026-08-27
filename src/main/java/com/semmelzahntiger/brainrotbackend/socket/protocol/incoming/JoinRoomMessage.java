package com.semmelzahntiger.brainrotbackend.socket.protocol.incoming;

import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;

public record JoinRoomMessage(String code) implements InboundNetworkMessage {}
