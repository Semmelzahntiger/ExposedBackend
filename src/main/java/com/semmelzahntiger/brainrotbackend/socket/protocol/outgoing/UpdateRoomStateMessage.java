package com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing;

import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UpdateRoomStateMessage(String roomCode, List<RoomPlayerRole> players, boolean hostIsReceiver, Settings settings) implements OutboundNetworkMessage {
    public record RoomPlayerRole(UUID playerUUID, String username, boolean isHost) {}
    public record Settings(
            int roomSize,
            int rounds,
            int roundTimeInSeconds,
            List<String> enabledPlatforms,
            List<String> enabledResources,
            LocalDate beforeDate
    ) {}
}
