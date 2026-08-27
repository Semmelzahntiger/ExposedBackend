package com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing;

import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;

public record DenyCreateRoomMessage(String reason) implements OutboundNetworkMessage {
}
