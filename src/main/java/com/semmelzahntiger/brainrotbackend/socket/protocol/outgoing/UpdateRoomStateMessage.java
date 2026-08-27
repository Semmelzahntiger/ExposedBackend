package com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing;

import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;

import java.util.List;
import java.util.UUID;

public record UpdateRoomStateMessage(String roomCode, List<RoomPlayerRole> players, boolean hostIsReceiver) implements OutboundNetworkMessage {
    public record RoomPlayerRole(UUID playerUUID, String username, boolean isHost) {}
}
