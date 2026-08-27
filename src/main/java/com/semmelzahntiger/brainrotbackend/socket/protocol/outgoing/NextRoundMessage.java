package com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing;

import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;

public record NextRoundMessage(String resourceType, String resource) implements OutboundNetworkMessage {}
