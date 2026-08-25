package com.semmelzahntiger.brainrotbackend.service.socket;


import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.UnreadableMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class SocketCommsHandlerComponent {
    private static final Logger log = LoggerFactory.getLogger(SocketCommsHandlerComponent.class);
    private final ObjectMapper objectMapper = new ObjectMapper();


    public void send(WebSocketSession session, OutboundNetworkMessage message) throws IOException {
        log.info("Sending '{}' to '{}'", message.getClass().getSimpleName(), session.getRemoteAddress());
        try {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        } catch (JacksonException e) {
            log.error("Error while parsing Message to JSON", e);
        }
    }
    public InboundNetworkMessage parseMessage(TextMessage textMessage) {
        try {
            String message = textMessage.getPayload();
            return objectMapper.readValue(message, InboundNetworkMessage.class);
        } catch (JacksonException e) {
            log.error("Couldn't decode Network Message", e);
            return new UnreadableMessage();
        }
    }
}
