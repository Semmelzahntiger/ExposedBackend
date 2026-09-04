package com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing;

import com.semmelzahntiger.brainrotbackend.service.util.InstagramResolver;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem;
import com.semmelzahntiger.brainrotbackend.socket.protocol.OutboundNetworkMessage;

import java.util.List;

public record NextRoundMessage(AbstractMediaItem mediaItem) implements OutboundNetworkMessage {}
