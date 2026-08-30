package com.semmelzahntiger.brainrotbackend.socket.protocol.incoming;

import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;

import java.time.LocalDate;
import java.util.List;

public record ChangeRoomSettings(
        int roomSize,
        int rounds,
        int roundTimeInSeconds,
        List<String> enabledPlatforms,
        List<String> enabledResources,
        LocalDate beforeDate
) implements InboundNetworkMessage {}
