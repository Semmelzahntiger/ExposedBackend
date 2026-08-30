package com.semmelzahntiger.brainrotbackend.socket.protocol.incoming;

import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;

import java.util.UUID;

public record SubmitGuessMessage(UUID guessedUUID) implements InboundNetworkMessage {}
