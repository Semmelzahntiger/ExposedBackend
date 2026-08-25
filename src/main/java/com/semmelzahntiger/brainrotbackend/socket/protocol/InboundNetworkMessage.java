package com.semmelzahntiger.brainrotbackend.socket.protocol;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.semmelzahntiger.brainrotbackend.socket.protocol.incoming.AuthenticationMessage;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = UnreadableMessage.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AuthenticationMessage.class, name = ProtocolDefinition.INCOMING_AUTHENTICATION_MESSAGE)
})
public interface IncomingNetworkMessage {

}
