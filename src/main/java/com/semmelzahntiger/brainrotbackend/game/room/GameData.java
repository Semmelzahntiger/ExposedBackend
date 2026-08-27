package com.semmelzahntiger.brainrotbackend.game.room;

import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class GameData {

    private Queue<UserEntry> entriesLeft = new ArrayDeque<>();
    @Getter
    @Setter
    private Instant roundStartedTimestamp = null;


    public GameData(List<UserEntry> entries) {
        entriesLeft.addAll(entries);
    }
    public UserEntry getNextEntry() {
        return entriesLeft.poll();
    }
    public boolean hasNext() {
        return !entriesLeft.isEmpty();
    }
    public boolean isReconnectablePlayer(GameUser gameUser) {
        return false;
    }
    public record UserEntry(GameUser user, String dataType, String ref) {}
    public record SubmittedGuess(GameUser by, UUID guessed, Instant at) {}
}
