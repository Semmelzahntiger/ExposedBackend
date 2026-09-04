package com.semmelzahntiger.brainrotbackend.game.room;

import com.semmelzahntiger.brainrotbackend.data.repositories.DataEntryRepository;
import com.semmelzahntiger.brainrotbackend.game.RoomSettings;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaResource;
import com.semmelzahntiger.brainrotbackend.service.util.ResolverService;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.AbstractMediaItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.MissingMediaItem;
import com.semmelzahntiger.brainrotbackend.service.util.cdn.StringMediaItem;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmChangeRoomSettings;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmSubmissionMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyChangeRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyStartGame;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenySubmissionMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.GameOverMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.NextRoundMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.StartedGameMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.UpdateGameScoreStateMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.UpdateRoomStateMessage;
import com.semmelzahntiger.brainrotbackend.util.LobbyCodeGenerator;
import com.semmelzahntiger.brainrotbackend.util.RandomUtil;
import com.semmelzahntiger.brainrotbackend.util.ThreadUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
@Slf4j
public class Room {
    private static final int MIN_ROOM_SIZE = 1;
    private static final int MAX_ROOM_SIZE = 10;
    private static final int MIN_PLAYERS_TO_START = 1;
    private static final int MIN_ROUNDS = 5;
    private static final int MAX_ROUNDS = 30;
    private static final int MIN_ROUND_TIME_SECONDS = 10;
    private static final int MAX_ROUND_TIME_SECONDS = 300;
    private static final int SCORE_SCREEN_SECONDS = 5;
    private static final Set<String> VALID_PLATFORMS = Arrays.stream(SocialMediaPlatform.values())
            .map(SocialMediaPlatform::getName).collect(Collectors.toSet());
    private static final Set<String> VALID_RESOURCES = Arrays.stream(SocialMediaResource.ResourceType.values())
            .map(SocialMediaResource.ResourceType::getName).collect(Collectors.toSet());
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
    private final ResolverService resolverService;
    @Getter
    protected volatile boolean gameStarted = false;
    @Getter
    protected @Nullable GameData game = null;
    protected int roundCounter = 0;
    protected int activeRoundToken = -1;
    protected @Nullable Future<?> roundTimeout = null;
    protected @Nullable Future<?> nextRoundTrigger = null;



    public Room(GameUser host, Consumer<String> onCloseCallback, DataEntryRepository dataEntryRepository, ResolverService resolverService) {
        this.host = host;
        this.onCloseCallback = onCloseCallback;
        this.dataEntryRepository = dataEntryRepository;
        this.resolverService = resolverService;
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
        cancelGameTimers();
        onCloseCallback.accept(roomCode);
        LobbyCodeGenerator.freeLobbyCode(roomCode);
    }
    protected void cancelGameTimers() {
        if(roundTimeout != null) {
            roundTimeout.cancel(false);
            roundTimeout = null;
        }
        if(nextRoundTrigger != null) {
            nextRoundTrigger.cancel(false);
            nextRoundTrigger = null;
        }
    }
    public CompletableFuture<Boolean> joinRoom(GameUser player) {
        CompletableFuture<Boolean> joined = new CompletableFuture<>();
        submitTask(() -> {
            if(stillOpen && (!gameStarted || (game != null && game.isReconnectablePlayer(player))) && !player.inRoom() && players.size() < settings.getRoomSize()) {
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
    public void submitGuess(GameUser player, UUID guessedUUID) {
        submitTask(() -> {
            if(!gameStarted || game == null) {
                player.getConnection().sendMessage(new DenySubmissionMessage("No game is currently running."));
                return;
            }
            GameData.GuessSubmissionResult result = game.submitGuess(player, guessedUUID);
            switch (result) {
                case ACCEPTED -> player.getConnection().sendMessage(new ConfirmSubmissionMessage());
                case NOT_PARTICIPANT -> player.getConnection().sendMessage(new DenySubmissionMessage("You are not part of this game."));
                case NO_ACTIVE_ROUND -> player.getConnection().sendMessage(new DenySubmissionMessage("There is no active round to guess in."));
                case ALREADY_SUBMITTED -> player.getConnection().sendMessage(new DenySubmissionMessage("You have already submitted a guess this round."));
            }
            if(result == GameData.GuessSubmissionResult.ACCEPTED && game.haveAllGuessed()) {
                completeRound(activeRoundToken);
            }
        });
    }
    public void skipRound(GameUser requester) {
        submitTask(() -> {
            if(!isHost(requester)) {
                log.warn("Non-host user tried to skip the round.");
                return;
            }
            if(!gameStarted || activeRoundToken == -1) {
                return;
            }
            completeRound(activeRoundToken);
        });
    }
    public void changeRoomSettings(GameUser requester, int roomSize, int rounds, int roundTimeInSeconds,
                                   List<String> enabledPlatforms, List<String> enabledResources, LocalDate beforeDate) {
        submitTask(() -> {
            if(!isHost(requester)) {
                requester.getConnection().sendMessage(new DenyChangeRoomMessage("Only the host can change the room settings."));
                return;
            }
            if(gameStarted) {
                requester.getConnection().sendMessage(new DenyChangeRoomMessage("You cannot change the settings while a game is running."));
                return;
            }
            if(roomSize < MIN_ROOM_SIZE || roomSize > MAX_ROOM_SIZE) {
                requester.getConnection().sendMessage(new DenyChangeRoomMessage("Room size must be between " + MIN_ROOM_SIZE + " and " + MAX_ROOM_SIZE + " players."));
                return;
            }
            if(roomSize < players.size()) {
                requester.getConnection().sendMessage(new DenyChangeRoomMessage("Room size cannot be smaller than the current player count."));
                return;
            }
            if(rounds < MIN_ROUNDS || rounds > MAX_ROUNDS) {
                requester.getConnection().sendMessage(new DenyChangeRoomMessage("Rounds must be between " + MIN_ROUNDS + " and " + MAX_ROUNDS + "."));
                return;
            }
            if(roundTimeInSeconds < MIN_ROUND_TIME_SECONDS || roundTimeInSeconds > MAX_ROUND_TIME_SECONDS) {
                requester.getConnection().sendMessage(new DenyChangeRoomMessage("Round time must be between " + MIN_ROUND_TIME_SECONDS + " and " + MAX_ROUND_TIME_SECONDS + " seconds."));
                return;
            }
            if(enabledPlatforms == null || !VALID_PLATFORMS.containsAll(enabledPlatforms)) {
                requester.getConnection().sendMessage(new DenyChangeRoomMessage("One or more selected platforms are unknown."));
                return;
            }
            if(enabledResources == null || !VALID_RESOURCES.containsAll(enabledResources)) {
                log.warn("Passed these Resources: {}", enabledResources);
                requester.getConnection().sendMessage(new DenyChangeRoomMessage("One or more selected resource types are unknown."));
                return;
            }
            settings.setRoomSize(roomSize);
            settings.setRounds(rounds);
            settings.setRoundTimeInSeconds(roundTimeInSeconds);
            settings.setEnabledPlatforms(enabledPlatforms);
            settings.setEnabledResources(enabledResources);
            settings.setBeforeDate(beforeDate);
            requester.getConnection().sendMessage(new ConfirmChangeRoomSettings());
            broadcastRoomUpdate();
        });
    }
    public void changeHost(GameUser requester, UUID newHostUUID) {
        submitTask(() -> {
            if(!isHost(requester)) {
                log.warn("Non-host user tried to reassign the room admin.");
                return;
            }
            GameUser newHost = players.get(newHostUUID);
            if(newHost == null) {
                log.warn("Host tried to assign admin to a user that's not in the room.");
                return;

            }
            host = newHost;
            broadcastRoomUpdate();
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
    public void startGame(GameUser requester) {
        submitTask(() -> {
            if(!isHost(requester)) {
                requester.getConnection().sendMessage(new DenyStartGame("Only the host can start the game."));
                return;
            }
            if(gameStarted) {
                log.warn("Host tried starting game that's already running.");
                return;
            }
            if(players.size() < MIN_PLAYERS_TO_START) {
                requester.getConnection().sendMessage(new DenyStartGame("You need at least " + MIN_PLAYERS_TO_START + " players to start the game."));
                return;
            }
            List<GameData.UserEntry> entries = dataEntryRepository.getRandomWithFilters(
                    players.keySet().stream().toList(),
                    getSettings().getEnabledResources(),
                    getSettings().getEnabledPlatforms(),
                    getSettings().getBeforeDate(),
                    getSettings().getRounds()
            ).stream().map(entry -> new GameData.UserEntry(players.get(entry.getUser_id()), entry.getPlatform(),entry.getDataType(), entry.getValue())).toList();
            if(entries.isEmpty()) {
                log.info("Game start aborted in room '{}': no data entries matched the current settings.", roomCode);
                host.getConnection().sendMessage(new DenyStartGame("No content could be found for the current settings. Adjust the platforms, resources, or date and try again."));
                return;
            }
            game = new GameData(players.values(), entries, getSettings().getRoundTimeInSeconds());
            gameStarted = true;
            runGameLoop();
        });
    }
    // Starts the Game Loop
    protected void runGameLoop() {
        List<StartedGameMessage.Participant> participants = game.getPlayers().stream()
                .map(player -> new StartedGameMessage.Participant(player.getUserUUID(), player.getUsername()))
                .toList();
        StartedGameMessage startedGameMessage = new StartedGameMessage(participants);
        for (GameUser player : game.getPlayers()) {
            player.getConnection().sendMessage(startedGameMessage);
        }
        advanceToNextRound();
    }

    // Initiates a Game Round. Initiates the timeout
    protected void advanceToNextRound() {
        if(!gameStarted || game == null) {
            return;
        }
        if(!game.next()) {
            endGame();
            return;
        }
        GameData.UserEntry userEntry = game.getCurrentEntry();
        AbstractMediaItem abstractMediaItem = resolveAbstractMediaItem(userEntry);
        if(abstractMediaItem == null) {
            abstractMediaItem = new MissingMediaItem(SocialMediaPlatform.getByPlatformName(userEntry.platform()));
        }
        game.initNextRoundData();
        roundCounter++;
        activeRoundToken = roundCounter;
        int token = activeRoundToken;
        broadcastNextRound(abstractMediaItem);
        roundTimeout = ThreadUtil.scheduleTask(() -> submitTask(() -> completeRound(token)),
                getSettings().getRoundTimeInSeconds(), TimeUnit.SECONDS);
    }
    private AbstractMediaItem resolveAbstractMediaItem(GameData.UserEntry currentEntry) {
        return switch (currentEntry.dataType()) {
            case "liked", "saved", "reposted" -> resolverService.resolveContents(currentEntry.ref());
            case "commented", "searched" -> new StringMediaItem(SocialMediaPlatform.getByPlatformName(currentEntry.platform()), currentEntry.ref());
            default -> null;
        };
    }
    // Completes Round. If somehow triggered by lingering timeout, return immediately. Cancels old timeouts. Initiates next round
    protected void completeRound(int token) {
        if(token != activeRoundToken) {
            return;
        }
        activeRoundToken = -1;
        if(roundTimeout != null) {
            roundTimeout.cancel(false);
            roundTimeout = null;
        }
        broadcastScoreState();
        nextRoundTrigger = ThreadUtil.scheduleTask(() -> submitTask(this::advanceToNextRound),
                SCORE_SCREEN_SECONDS, TimeUnit.SECONDS);
    }
    protected void endGame() {
        cancelGameTimers();
        broadcastGameOver();
        gameStarted = false;
        game = null;
        activeRoundToken = -1;
    }
    protected void broadcastScoreState() {
        List<UpdateGameScoreStateMessage.RoomPlayer> playerScores = game.getPlayers().stream()
                .map(player -> new UpdateGameScoreStateMessage.RoomPlayer(player.getUserUUID(), player.getUsername(), game.getScoreFrom(player), game.getRoundScoreFrom(player)))
                .toList();
        for (GameUser value : game.getPlayers()) {
            value.getConnection().sendMessage(new UpdateGameScoreStateMessage(value.getUserUUID(), playerScores));
        }
    }

    protected void broadcastRoomUpdate() {
        List<UpdateRoomStateMessage.RoomPlayerRole> playerRoles = players.values().stream()
                .map(player -> new UpdateRoomStateMessage.RoomPlayerRole(player.getUserUUID(), player.getUsername(), isHost(player))).toList();
        UpdateRoomStateMessage.Settings settingsState = new UpdateRoomStateMessage.Settings(
                settings.getRoomSize(),
                settings.getRounds(),
                settings.getRoundTimeInSeconds(),
                settings.getEnabledPlatforms(),
                settings.getEnabledResources(),
                settings.getBeforeDate()
        );
        for (GameUser player : players.values()) {
            boolean playerIsHost = isHost(player);
            player.getConnection().sendMessage(new UpdateRoomStateMessage(getRoomCode(), playerRoles, playerIsHost, settingsState));
        }
    }
    protected void broadcastNextRound(AbstractMediaItem mediaItem) {
        if(game == null || game.getCurrentEntry() == null) {
            log.error("BroadcastNextRound Message occurred outside of intended game cycle.");
        }
        NextRoundMessage nextRoundMessage = new NextRoundMessage(mediaItem);
        for (GameUser value : game.getPlayers()) {
            value.getConnection().sendMessage(nextRoundMessage);
        }
    }
    protected void broadcastGameOver() {
        List<GameUser> winners = game.getWinner();
        List<GameData.UserScore> scores = game.getScores();
        for (GameUser player : game.getPlayers()) {
            player.getConnection().sendMessage(new GameOverMessage(winners.contains(player), player.getUserUUID(), scores));
        }
    }
    protected boolean isHost(GameUser player) {
        return host.equals(player);
    }
}
