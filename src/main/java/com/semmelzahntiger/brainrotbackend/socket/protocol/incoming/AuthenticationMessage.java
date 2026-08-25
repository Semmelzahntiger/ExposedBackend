package com.semmelzahntiger.brainrotbackend.socket.protocol.incoming;

import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;

public record AuthenticationMessage(String type, String token) implements InboundNetworkMessage {}
