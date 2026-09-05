package com.semmelzahntiger.brainrotbackend.game.room;

import com.semmelzahntiger.brainrotbackend.data.entities.DataEntry;
import com.semmelzahntiger.brainrotbackend.data.repositories.DataEntryRepository;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.service.stream.TikTokStreamingService;
import com.semmelzahntiger.brainrotbackend.service.util.ResolverService;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomManager {
    private final DataEntryRepository dataEntryRepository;
    private final ResolverService resolverService;
    private final TikTokStreamingService tikTokStreamingService;
    public Map<String, Room> lobbyCodeRoomMap = new ConcurrentHashMap<>();

    public RoomManager(DataEntryRepository repository, ResolverService resolverService, TikTokStreamingService tikTokStreamingService) {
        this.dataEntryRepository = repository;
        this.resolverService = resolverService;
        this.tikTokStreamingService = tikTokStreamingService;
    }

    public Room createRoom(GameUser creator) {
        Room room = new Room(creator, this::closeRoom, dataEntryRepository, resolverService, tikTokStreamingService);
        lobbyCodeRoomMap.put(room.getRoomCode(), room);
        return room;
    }

    public void closeRoom(String code) {
        lobbyCodeRoomMap.remove(code);
    }
    public @Nullable Room findRoom(String lobbyCode) {
        return lobbyCodeRoomMap.get(lobbyCode);
    }

}
