package com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing;

import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;

import java.util.List;
import java.util.UUID;

public record StartedGameMessage(List<Participant> participants) implements OutboundNetworkMessage {
    public record Participant(UUID uuid, String username) {}
}
