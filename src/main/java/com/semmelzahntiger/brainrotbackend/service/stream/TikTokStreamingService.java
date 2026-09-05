package com.semmelzahntiger.brainrotbackend.service.stream;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TikTokStreamingService {
    private final Map<String, RoomStreamingStore> streamStore = new ConcurrentHashMap<>();

    public @Nullable StreamData getStreamDataFor(String roomId, String postId) {
        RoomStreamingStore store = streamStore.get(roomId);
        return store != null ? store.getStreamData(postId) : null;
    }
    public void addStreamData(String roomId, String postId, String cdn, String ttChainToken) {
        streamStore.computeIfAbsent(roomId, _ -> new RoomStreamingStore()).addStreamData(postId, cdn, ttChainToken);
    }
    public void deleteRoomStreamData(String roomId) {
        streamStore.remove(roomId);
    }

    public record StreamData(String cdn, String ttChainCookie){}

    private static class RoomStreamingStore {
        // Probably don't need a concurrent hashmap. Each room has its own data which no other room accesses. Just in case lol.
        private final Map<String, StreamData> roomStreamData = new ConcurrentHashMap<>();
        private void addStreamData(String postId, String cdn, String ttChainToken) {
            roomStreamData.put(postId, new StreamData(cdn, ttChainToken));
        }
        private StreamData getStreamData(String postId) {
            return roomStreamData.get(postId);
        }
    }
}
