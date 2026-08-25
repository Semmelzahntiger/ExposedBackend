package com.semmelzahntiger.brainrotbackend.service.socket;

import com.semmelzahntiger.brainrotbackend.socket.protocol.InboundNetworkMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface SocketMessageHandler {
    InboundNetworkMessage parseMessage(TextMessage message);
}
