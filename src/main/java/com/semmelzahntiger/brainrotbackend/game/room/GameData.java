package com.semmelzahntiger.brainrotbackend.game.room;

import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class GameData {
    private static final double GRACE_FRACTION = 0.10;
    private static final double PITY_START_FRACTION = 0.90;
    private static final int MAX_SCORE = 1000;
    private static final float MINIMUM_SCORE_MULTIPLIER = 0.5f;
    private Map<GameUser, Integer> playersScore = new HashMap<>();
    private Map<GameUser, Integer> roundPoints = new HashMap<>();
    private Map<GameUser, Boolean> guessWasCorrect = new HashMap<>();
    private int correctGuessSum = 0;


    private Queue<UserEntry> entriesLeft = new ArrayDeque<>();
    @Getter
    @Setter
    private Instant roundStartedTimestamp = null;
    @Getter
    private UserEntry currentEntry = null;
    private final int roundTime;


    public GameData(Collection<GameUser> players, List<UserEntry> entries, int roundTime) {
        for (GameUser player : players) {
            playersScore.put(player, 0);
            roundPoints.put(player, 0);
        }
        entriesLeft.addAll(entries);
        this.roundTime = roundTime;
    }
    public boolean next() {
        currentEntry = entriesLeft.poll();
        guessWasCorrect.clear();
        return currentEntry != null;
    }
    public boolean isReconnectablePlayer(GameUser gameUser) {
        return false;
    }
    public void initNextRoundData() {
        setRoundStartedTimestamp(Instant.now());
        correctGuessSum = 0;
        playersScore.keySet().forEach(player -> roundPoints.put(player, 0));
    }
    public GuessSubmissionResult submitGuess(GameUser by, UUID guessed) {
        if(!playersScore.containsKey(by)) {
            log.warn("User tried to submit guess that's not a participant");
            return GuessSubmissionResult.NOT_PARTICIPANT;
        }
        if(currentEntry == null) {
            return GuessSubmissionResult.NO_ACTIVE_ROUND;
        }
        if(guessWasCorrect.containsKey(by)) {
            return GuessSubmissionResult.ALREADY_SUBMITTED;
        }
        Instant guessTime = Instant.now();
        if(!guessed.equals(currentEntry.user.getUserUUID())) {
            guessWasCorrect.put(by, false);
            return GuessSubmissionResult.ACCEPTED;
        }
        int score = calculateScore(roundStartedTimestamp, guessTime) - calculatePenalty();
        guessWasCorrect.put(by, true);
        playersScore.put(by, playersScore.get(by) + score);
        roundPoints.put(by, score);
        correctGuessSum++;
        return GuessSubmissionResult.ACCEPTED;
    }
    private int calculateScore(Instant roundStartTimeStamp, Instant submissionTime) {
        double elapsed = Math.max(Duration.between(roundStartTimeStamp, submissionTime).toMillis() / 1000.0, 0);
        double graceEnd = roundTime * GRACE_FRACTION;
        double pityStart = roundTime * PITY_START_FRACTION;
        if(elapsed <= graceEnd) {
            return MAX_SCORE;
        }
        if(elapsed >= pityStart) {
            return (int) (MAX_SCORE * MINIMUM_SCORE_MULTIPLIER);
        }
        double progress = (elapsed - graceEnd) / (pityStart - graceEnd);
        return (int) (MAX_SCORE * (1 - progress * (1 - MINIMUM_SCORE_MULTIPLIER)));
    }
    private int calculatePenalty() {
        int players = playersScore.size();
        return 250 * (correctGuessSum / (players -1));
    }
    public int getScoreFrom(GameUser player) {
        return playersScore.get(player);
    }
    public int getRoundScoreFrom(GameUser player) {
        return roundPoints.getOrDefault(player, 0);
    }
    public Set<GameUser> getPlayers() {
        return playersScore.keySet();
    }
    public boolean haveAllGuessed() {
        return guessWasCorrect.size() == playersScore.size();
    }
    public List<GameUser> getWinner() {
        List<GameUser> winners = new ArrayList<>();
        int maxScore = Integer.MIN_VALUE;
        for (Map.Entry<GameUser, Integer> entry : playersScore.entrySet()) {
            GameUser player = entry.getKey();
            int score = entry.getValue();
            if (score > maxScore) {
                maxScore = score;
                winners.clear();
                winners.add(player);
            } else if (score == maxScore) {
                winners.add(player);
            }
        }
        return winners;
    }
    public List<UserScore> getScores() {
        return playersScore.entrySet().stream().map(userScore -> new UserScore(userScore.getKey().getUserUUID(), userScore.getValue())).toList();
    }

    public record UserEntry(GameUser user,String platform, String dataType, String ref) {}
    public record UserScore(UUID user, int score) {}

    public enum GuessSubmissionResult {
        ACCEPTED,
        NOT_PARTICIPANT,
        NO_ACTIVE_ROUND,
        ALREADY_SUBMITTED
    }

}
