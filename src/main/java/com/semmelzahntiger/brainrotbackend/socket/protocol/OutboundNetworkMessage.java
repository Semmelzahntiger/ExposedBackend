package com.semmelzahntiger.brainrotbackend.socket.protocol;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmAuthenticationMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmChangeRoomSettings;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmCreateRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmJoinRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmLeaveRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmSubmissionMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyAuthenticationMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenySubmissionMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyChangeRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyCreateRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyJoinRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyStartGame;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.GameOverMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.UpdateGameScoreStateMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.MultiGuessResultMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.NextRoundMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.RoomNotFoundMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.StartedGameMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.UpdateRoomStateMessage;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = DenyRequestMessage.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConfirmAuthenticationMessage.class, name = ProtocolDefinition.CONFIRM_AUTHENTICATION_MESSAGE),
        @JsonSubTypes.Type(value = DenyAuthenticationMessage.class, name = ProtocolDefinition.DENY_AUTHENTICATION_MESSAGE),
        @JsonSubTypes.Type(value = ConfirmJoinRoomMessage.class, name = ProtocolDefinition.CONFIRM_JOIN_ROOM_MESSAGE),
        @JsonSubTypes.Type(value = DenyJoinRoomMessage.class, name = ProtocolDefinition.DENY_JOIN_ROOM_MESSAGE),
        @JsonSubTypes.Type(value = ConfirmChangeRoomSettings.class, name = ProtocolDefinition.CONFIRM_CHANGE_ROOM_SETTINGS),
        @JsonSubTypes.Type(value = DenyChangeRoomMessage.class, name = ProtocolDefinition.DENY_CHANGE_ROOM_MESSAGE),
        @JsonSubTypes.Type(value = StartedGameMessage.class, name = ProtocolDefinition.STARTED_GAME_MESSAGE),
        @JsonSubTypes.Type(value = DenyStartGame.class, name = ProtocolDefinition.DENY_START_GAME),
        @JsonSubTypes.Type(value = UpdateRoomStateMessage.class, name = ProtocolDefinition.UPDATE_ROOM_STATE_MESSAGE),
        @JsonSubTypes.Type(value = NextRoundMessage.class, name = ProtocolDefinition.NEXT_ROUND_MESSAGE),
        @JsonSubTypes.Type(value = MultiGuessResultMessage.class, name = ProtocolDefinition.MULTI_GUESS_RESULT_MESSAGE),
        @JsonSubTypes.Type(value = UpdateGameScoreStateMessage.class, name = ProtocolDefinition.GAME_SCORE_STATE_MESSAGE),
        @JsonSubTypes.Type(value = GameOverMessage.class, name = ProtocolDefinition.GAME_OVER_MESSAGE),
        @JsonSubTypes.Type(value = RoomNotFoundMessage.class, name = ProtocolDefinition.ROOM_NOT_FOUND_MESSAGE),
        @JsonSubTypes.Type(value = ConfirmCreateRoomMessage.class, name = ProtocolDefinition.CONFIRM_CREATE_ROOM_MESSAGE),
        @JsonSubTypes.Type(value = DenyCreateRoomMessage.class, name = ProtocolDefinition.DENY_CREATE_ROOM_MESSAGE),
        @JsonSubTypes.Type(value = ConfirmLeaveRoomMessage.class, name = ProtocolDefinition.CONFIRM_LEAVE_ROOM_MESSAGE),
        @JsonSubTypes.Type(value = ConfirmSubmissionMessage.class, name = ProtocolDefinition.CONFIRM_SUBMISSION_MESSAGE),
        @JsonSubTypes.Type(value = DenySubmissionMessage.class, name = ProtocolDefinition.DENY_SUBMISSION_MESSAGE)
})
public interface OutboundNetworkMessage {}
