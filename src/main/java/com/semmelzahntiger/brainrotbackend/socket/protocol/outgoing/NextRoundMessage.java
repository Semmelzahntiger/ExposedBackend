package com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing;

import com.semmelzahntiger.brainrotbackend.service.util.InstagramResolver;
import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;

import java.util.List;

public record NextRoundMessage(String platform, String resourceType, String resource) implements OutboundNetworkMessage {
    public record Resource(String mediaType, String url){}
}
