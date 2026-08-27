package com.semmelzahntiger.brainrotbackend.game.room;

import com.semmelzahntiger.brainrotbackend.data.repositories.DataEntryRepository;
import com.semmelzahntiger.brainrotbackend.game.RoomSettings;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.StartedGameMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.UpdateRoomStateMessage;
import com.semmelzahntiger.brainrotbackend.util.LobbyCodeGenerator;
import com.semmelzahntiger.brainrotbackend.util.RandomUtil;
import com.semmelzahntiger.brainrotbackend.util.ThreadUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
@Slf4j
public class Room {
    @Getter
    protected boolean stillOpen = true;
    @Getter
    protected final RoomSettings settings = new RoomSettings();
    @Getter
    protected final @NotNull String roomCode = LobbyCodeGenerator.generateCode();
    @Getter
    protected volatile @NotNull GameUser host;
    protected final Map<UUID, GameUser> players = new HashMap<>();
    protected final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    protected final CountDownLatch roomClosure = new CountDownLatch(1);
    protected final Consumer<String> onCloseCallback;
    private final DataEntryRepository dataEntryRepository;
    @Getter
    protected volatile boolean gameStarted = false;
    @Getter
    protected @Nullable GameData game = null;
    protected ScheduledFuture<?> runningGame = null;



    public Room(GameUser host, Consumer<String> onCloseCallback, DataEntryRepository dataEntryRepository) {
        this.host = host;
        this.onCloseCallback = onCloseCallback;
        this.dataEntryRepository = dataEntryRepository;
        runRoomLoop();
        joinRoom(host);
    }
    protected void runRoomLoop() {
        ThreadUtil.runTaskAsync(() -> {
           while(roomClosure.getCount() > 0) {
               try {
                   Runnable runnable = taskQueue.take();
                   runnable.run();
               } catch (InterruptedException e) {
                   log.error("Exception occured in the Room");
               }

           }
           onClose();
        });
    }
    protected final void submitTask(Runnable runnable) {
        taskQueue.add(runnable);
    }
    protected void onClose() {
        onCloseCallback.accept(roomCode);
        LobbyCodeGenerator.freeLobbyCode(roomCode);
    }
    public CompletableFuture<Boolean> joinRoom(GameUser player) {
        CompletableFuture<Boolean> joined = new CompletableFuture<>();
        submitTask(() -> {
            if((!gameStarted || (game != null && game.isReconnectablePlayer(player))) && !player.inRoom() && players.size() < settings.getRoomSize()) {
                players.put(player.getUserUUID(), player);
                player.setRoom(this);
                joined.complete(true);
                broadcastRoomUpdate();
            }
            else {
                joined.complete(false);
                if(players.isEmpty()) {
                    stillOpen = false;
                    roomClosure.countDown();
                }
            }
        });
        return joined;
    }
    public void leaveRoom(GameUser player) {
        submitTask(() -> {
            if(this.equals(player.getCurrentRoom())) {
                players.remove(player.getUserUUID());
                player.setRoom(null);
                selectNewAdmin();
                broadcastRoomUpdate();
            }
            if(players.isEmpty()) {
                stillOpen = false;
                roomClosure.countDown();
            }
        });
    }
    protected void selectNewAdmin() {
        List<GameUser> candidates = players.values().stream().toList();
        if(candidates.isEmpty()) {
            return;
        }
        int bound = candidates.size();
        int newHostIdx = RandomUtil.getRandomBetweenSize(bound);
        host = candidates.get(newHostIdx);
    }
    public void startGame() {
        submitTask(() -> {
            List<GameData.UserEntry> entries = dataEntryRepository.getRandomWithFilters(
                    players.keySet().stream().toList(),
                    getSettings().getEnabledResources(),
                    getSettings().getEnabledPlatforms(),
                    getSettings().getBeforeDate(),
                    getSettings().getRounds()
            ).stream().map(entry -> new GameData.UserEntry(players.get(entry.getUser_id()), entry.getDataType(), entry.getValue())).toList();
            game = new GameData(entries);
            runGameLoop();
        });
    }
    protected void runGameLoop() {
        for (GameUser player : players.values()) {
            player.getConnection().sendMessage(new StartedGameMessage());
        }
        runningGame = ThreadUtil.scheduleTaskWithRate(() -> {
            endRound();
            try {
                Thread.sleep(Duration.ofSeconds(3));
            } catch (InterruptedException e) {
                log.error("Sleep Delay interrupted.", e);
            }
            nextRound();
        }, 2,  getSettings().getRoundTimeInSeconds(), TimeUnit.SECONDS);
    }
    protected void endGame() {
        runningGame.cancel(true);
    }
    protected void endRound() {
        submitTask(() -> {

        });
    }
    protected void nextRound() {
        submitTask(() -> {

        });
    }

    protected void broadcastRoomUpdate() {
        List<UpdateRoomStateMessage.RoomPlayerRole> playerRoles = players.values().stream()
                .map(player -> new UpdateRoomStateMessage.RoomPlayerRole(player.getUserUUID(), player.getUsername(), isHost(player))).toList();
        for (GameUser player : players.values()) {
            boolean playerIsHost = isHost(player);
            player.getConnection().sendMessage(new UpdateRoomStateMessage(getRoomCode(), playerRoles, playerIsHost));
        }
    }
    protected boolean isHost(GameUser player) {
        return host.equals(player);
    }
}
