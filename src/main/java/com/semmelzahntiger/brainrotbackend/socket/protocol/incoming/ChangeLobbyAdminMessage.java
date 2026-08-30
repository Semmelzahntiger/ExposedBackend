package com.semmelzahntiger.brainrotbackend.socket.protocol.incoming;

import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;

import java.util.UUID;

public record ChangeLobbyAdminMessage(UUID newAdminUUID) implements InboundNetworkMessage {}
