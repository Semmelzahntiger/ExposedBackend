package com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing;

import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;

import java.util.List;
import java.util.UUID;

public record UpdateGameScoreStateMessage(List<RoomPlayer> players) implements OutboundNetworkMessage {
    public record RoomPlayer(UUID playerUUID, String username, int score) {}
}
