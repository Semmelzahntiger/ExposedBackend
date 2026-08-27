package com.semmelzahntiger.brainrotbackend.game.room;

import com.semmelzahntiger.brainrotbackend.data.entities.DataEntry;
import com.semmelzahntiger.brainrotbackend.data.repositories.DataEntryRepository;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomManager {
    private final DataEntryRepository dataEntryRepository;
    public Map<String, Room> lobbyCodeRoomMap = new ConcurrentHashMap<>();

    public RoomManager(DataEntryRepository repository) {
        this.dataEntryRepository = repository;
    }

    public Room createRoom(GameUser creator) {
        Room room = new Room(creator, this::closeRoom, dataEntryRepository);
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
