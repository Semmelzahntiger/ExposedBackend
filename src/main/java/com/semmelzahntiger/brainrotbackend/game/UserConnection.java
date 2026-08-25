package com.semmelzahntiger.brainrotbackend.game;

import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;
import com.semmelzahntiger.brainrotbackend.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.core.JacksonException;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
public class UserConnection {
    protected final WebSocketSession socketSession;

    public UserConnection(WebSocketSession session) {
        this.socketSession = session;
    }
    public String getSessionId() {
        return socketSession.getId();
    }
    public InetSocketAddress getRemoteAddress() {
        return socketSession.getRemoteAddress();
    }
    public void close(CloseStatus status) {
        try {
            if(status == null) {
                socketSession.close();
            }
            else {
                socketSession.close(status);
            }
        } catch (IOException e) {
            log.warn("Couldn't close socket. Socket is likely already closed");
        }

    }
    public void sendMessage(OutboundNetworkMessage message) {
        log.info("Sending '{}' to '{}'", message.getClass().getSimpleName(), socketSession.getRemoteAddress());
        try {
            String json = MapperUtil.MESSAGE_MAPPER.writeValueAsString(message);
            socketSession.sendMessage(new TextMessage(json));
        } catch (JacksonException e) {
            log.error("Error while parsing Message to JSON", e);
        } catch (IOException e) {
            close(CloseStatus.SERVER_ERROR);
        }
    }
}
