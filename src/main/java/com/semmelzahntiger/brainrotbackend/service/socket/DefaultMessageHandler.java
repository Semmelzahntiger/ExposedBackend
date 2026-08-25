package com.semmelzahntiger.brainrotbackend.service.socket;


import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.UnreadableMessage;
import com.semmelzahntiger.brainrotbackend.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Primary
@Component("default")
@Slf4j
public class DefaultMessageHandler implements SocketMessageHandler {
    public InboundNetworkMessage parseMessage(TextMessage textMessage) {
        try {
            String message = textMessage.getPayload();
            log.info(message);
            return MapperUtil.MESSAGE_MAPPER.readValue(message, InboundNetworkMessage.class);
        } catch (JacksonException e) {
            log.error("Couldn't decode Network Message", e);
            return new UnreadableMessage();
        }
    }
}
