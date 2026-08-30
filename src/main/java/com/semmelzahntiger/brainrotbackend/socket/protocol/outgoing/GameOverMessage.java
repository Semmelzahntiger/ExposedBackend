package com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing;

import com.semmelzahntiger.brainrotbackend.game.room.GameData;
import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;

import java.util.List;
import java.util.UUID;

public record GameOverMessage(boolean isWinner, UUID ownUUID, List<GameData.UserScore> scores) implements OutboundNetworkMessage {
}
