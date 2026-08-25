package com.semmelzahntiger.brainrotbackend.socket.protocol;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.AuthenticationMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.ChangeLobbyAdminMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.ChangeRoleMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.ChangeRoomSettings;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.CreateRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.JoinRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.LeaveRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.ReturnToLobbyMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.StartGameMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.SubmitGuessMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.SubmitMultiGuessMessage;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = UnreadableMessage.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthenticationMessage.class, name = ProtocolDefinition.INCOMING_AUTHENTICATION_MESSAGE),
        @JsonSubTypes.Type(value = CreateRoomMessage.class, name = ProtocolDefinition.INCOMING_CREATE_ROOM_MESSAGE),
        @JsonSubTypes.Type(value = JoinRoomMessage.class, name = ProtocolDefinition.INCOMING_JOIN_ROOM_MESSAGE),
        @JsonSubTypes.Type(value = LeaveRoomMessage.class, name = ProtocolDefinition.INCOMING_LEAVE_ROOM_MESSAGE),
        @JsonSubTypes.Type(value = ChangeRoomSettings.class, name = ProtocolDefinition.INCOMING_CHANGE_ROOM_SETTINGS),
        @JsonSubTypes.Type(value = StartGameMessage.class, name = ProtocolDefinition.INCOMING_START_GAME_MESSAGE),
        @JsonSubTypes.Type(value = ReturnToLobbyMessage.class, name = ProtocolDefinition.INCOMING_RETURN_TO_LOBBY_MESSAGE),
        @JsonSubTypes.Type(value = ChangeRoleMessage.class, name = ProtocolDefinition.INCOMING_CHANGE_ROLE_MESSAGE),
        @JsonSubTypes.Type(value = SubmitGuessMessage.class, name = ProtocolDefinition.INCOMING_SUBMIT_GUESS_MESSAGE),
        @JsonSubTypes.Type(value = SubmitMultiGuessMessage.class, name = ProtocolDefinition.INCOMING_SUBMIT_MULTI_GUESS_MESSAGE),
        @JsonSubTypes.Type(value = ChangeLobbyAdminMessage.class, name = ProtocolDefinition.INCOMING_CHANGE_LOBBY_ADMIN_MESSAGE)
})
public interface InboundNetworkMessage {}
