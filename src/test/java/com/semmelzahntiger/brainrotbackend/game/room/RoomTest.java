package com.semmelzahntiger.brainrotbackend.game.room;

import com.semmelzahntiger.brainrotbackend.data.AppUser;
import com.semmelzahntiger.brainrotbackend.data.repositories.DataEntryRepository;
import com.semmelzahntiger.brainrotbackend.game.UserConnection;
import com.semmelzahntiger.brainrotbackend.game.auth.GameUser;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaPlatform;
import com.semmelzahntiger.brainrotbackend.service.data.SocialMediaResource;
import com.semmelzahntiger.brainrotbackend.service.stream.TikTokStreamingService;
import com.semmelzahntiger.brainrotbackend.service.util.ResolverService;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.ConfirmChangeRoomSettings;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.DenyChangeRoomMessage;
import com.semmelzahntiger.brainrotbackend.socket.protocol.outgoing.UpdateRoomStateMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Exercises the lobby side of {@link Room} (membership, closure, settings, admin swap) without
 * touching the game itself. Lives in Room's own package so it can observe the protected
 * {@code players} map and use {@code submitTask} as a barrier to await the async room loop.
 */
@ExtendWith(MockitoExtension.class)
class RoomTest {

    private static final List<String> PLATFORMS = List.of(
            SocialMediaPlatform.INSTAGRAM.getName(), SocialMediaPlatform.TIKTOK.getName());
    private static final List<String> RESOURCES = List.of(
            SocialMediaResource.ResourceType.LIKED.getName(), SocialMediaResource.ResourceType.SAVED.getName());

    @Mock
    private DataEntryRepository dataEntryRepository;
    @Mock
    private ResolverService resolverService;
    @Mock
    private TikTokStreamingService tikTokStreamingService;

    /** Completed by the close callback with the closed room's code. */
    private CompletableFuture<String> closed;
    private Room room;
    private GameUser host;

    @BeforeEach
    void setUp() {
        closed = new CompletableFuture<>();
        host = user("host");
        room = new Room(host, code -> closed.complete(code), dataEntryRepository, resolverService, tikTokStreamingService);
        flush(); // wait for the constructor's async host-join to land
    }

    // ---------------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------------

    private GameUser user(String name) {
        UserConnection connection = mock(UserConnection.class);
        GameUser gameUser = new GameUser(connection);
        gameUser.authenticateAs(new AppUser(UUID.randomUUID(), name, name + "@example.com", List.of("USER")));
        return gameUser;
    }

    private boolean join(GameUser player) throws Exception {
        boolean result = room.joinRoom(player).get(2, TimeUnit.SECONDS);
        // joinRoom completes its future before broadcasting, so flush to let the whole task finish.
        flush();
        return result;
    }

    private void changeSettings(GameUser requester, int roomSize, int rounds, int roundTime) {
        room.changeRoomSettings(requester, roomSize, rounds, roundTime, PLATFORMS, RESOURCES, LocalDate.now());
        flush();
    }

    /** Submits a barrier task and waits for it, guaranteeing every earlier loop task has run. */
    private void flush() {
        CompletableFuture<Void> barrier = new CompletableFuture<>();
        room.submitTask(() -> barrier.complete(null));
        barrier.orTimeout(2, TimeUnit.SECONDS).join();
    }

    // ---------------------------------------------------------------------
    // creation & joining
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("a freshly created room contains the host as its only member and admin")
    void newRoom_hostIsMemberAndAdmin() {
        assertThat(room.players).containsOnlyKeys(host.getUserUUID());
        assertThat(room.getHost()).isSameAs(host);
        assertThat(room.isStillOpen()).isTrue();
        assertThat(host.inRoom()).isTrue();
        assertThat(host.getCurrentRoom()).isSameAs(room);
    }

    @Test
    @DisplayName("joining adds the player and links them to the room")
    void joinRoom_addsPlayer() throws Exception {
        GameUser player = user("player");

        assertThat(join(player)).isTrue();

        assertThat(room.players).containsKey(player.getUserUUID()).hasSize(2);
        assertThat(player.getCurrentRoom()).isSameAs(room);
    }

    @Test
    @DisplayName("joining broadcasts a room-state update to the new player")
    void joinRoom_broadcastsRoomUpdate() throws Exception {
        GameUser player = user("player");

        join(player);

        verify(player.getConnection()).sendMessage(isA(UpdateRoomStateMessage.class));
    }

    @Test
    @DisplayName("a player already in the room cannot join again")
    void joinRoom_whenAlreadyMember_isRejected() throws Exception {
        GameUser player = user("player");
        join(player);

        assertThat(join(player)).isFalse();
        assertThat(room.players).hasSize(2);
    }

    @Test
    @DisplayName("joining is rejected once the room is at capacity")
    void joinRoom_whenFull_isRejected() throws Exception {
        changeSettings(host, 2, 10, 30); // shrink capacity to host + 1
        assertThat(join(user("second"))).isTrue();

        assertThat(join(user("third"))).isFalse();
        assertThat(room.players).hasSize(2);
    }

    // ---------------------------------------------------------------------
    // leaving & closure
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("a non-host leaving is removed and unlinked, room stays open")
    void leaveRoom_removesPlayer() throws Exception {
        GameUser player = user("player");
        join(player);

        room.leaveRoom(player);
        flush();

        assertThat(room.players).containsOnlyKeys(host.getUserUUID());
        assertThat(player.getCurrentRoom()).isNull();
        assertThat(room.getHost()).isSameAs(host);
        assertThat(room.isStillOpen()).isTrue();
    }

    @Test
    @DisplayName("when the host leaves, admin is reassigned to a remaining member")
    void leaveRoom_hostLeaves_reassignsAdmin() throws Exception {
        GameUser player = user("player");
        join(player);

        room.leaveRoom(host);
        flush();

        assertThat(room.players).containsOnlyKeys(player.getUserUUID());
        assertThat(room.getHost()).isSameAs(player);
        assertThat(room.isStillOpen()).isTrue();
    }

    @Test
    @DisplayName("the last player leaving closes the room and fires the close callback")
    void leaveRoom_lastPlayer_closesRoom() throws Exception {
        room.leaveRoom(host);

        assertThat(closed.get(2, TimeUnit.SECONDS)).isEqualTo(room.getRoomCode());
        assertThat(room.isStillOpen()).isFalse();
    }

    @Test
    @DisplayName("a non-member calling leave neither removes anyone nor closes the room")
    void leaveRoom_byNonMember_isNoOp() {
        GameUser stranger = user("stranger");

        room.leaveRoom(stranger);
        flush();

        assertThat(room.players).containsOnlyKeys(host.getUserUUID());
        assertThat(room.isStillOpen()).isTrue();
        assertThat(closed).isNotDone();
    }

    // ---------------------------------------------------------------------
    // settings: valid updates
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("the host can apply a valid settings update and is confirmed")
    void changeRoomSettings_valid_appliesAndConfirms() {
        changeSettings(host, 6, 20, 60);

        assertThat(room.getSettings().getRoomSize()).isEqualTo(6);
        assertThat(room.getSettings().getRounds()).isEqualTo(20);
        assertThat(room.getSettings().getRoundTimeInSeconds()).isEqualTo(60);
        verify(host.getConnection()).sendMessage(isA(ConfirmChangeRoomSettings.class));
    }

    @Test
    @DisplayName("minimum boundary values (size 2, rounds 5, time 10) are accepted")
    void changeRoomSettings_minBoundaries_accepted() {
        changeSettings(host, 2, 5, 10);

        assertThat(room.getSettings().getRoomSize()).isEqualTo(2);
        assertThat(room.getSettings().getRounds()).isEqualTo(5);
        assertThat(room.getSettings().getRoundTimeInSeconds()).isEqualTo(10);
        verify(host.getConnection()).sendMessage(isA(ConfirmChangeRoomSettings.class));
    }

    @Test
    @DisplayName("maximum boundary values (size 10, rounds 30, time 300) are accepted")
    void changeRoomSettings_maxBoundaries_accepted() {
        changeSettings(host, 10, 30, 300);

        assertThat(room.getSettings().getRoomSize()).isEqualTo(10);
        assertThat(room.getSettings().getRounds()).isEqualTo(30);
        assertThat(room.getSettings().getRoundTimeInSeconds()).isEqualTo(300);
        verify(host.getConnection()).sendMessage(isA(ConfirmChangeRoomSettings.class));
    }

    // ---------------------------------------------------------------------
    // settings: threshold rejections
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("room size below 2 is rejected and leaves settings unchanged")
    void changeRoomSettings_roomSizeBelowMin_denied() {
        int before = room.getSettings().getRoomSize();

        changeSettings(host, 1, 10, 30);

        assertThat(room.getSettings().getRoomSize()).isEqualTo(before);
        verify(host.getConnection()).sendMessage(isA(DenyChangeRoomMessage.class));
    }

    @Test
    @DisplayName("room size above 10 is rejected and leaves settings unchanged")
    void changeRoomSettings_roomSizeAboveMax_denied() {
        int before = room.getSettings().getRoomSize();

        changeSettings(host, 11, 10, 30);

        assertThat(room.getSettings().getRoomSize()).isEqualTo(before);
        verify(host.getConnection()).sendMessage(isA(DenyChangeRoomMessage.class));
    }

    @Test
    @DisplayName("rounds below 5 is rejected and leaves settings unchanged")
    void changeRoomSettings_roundsBelowMin_denied() {
        int before = room.getSettings().getRounds();

        changeSettings(host, 8, 4, 30);

        assertThat(room.getSettings().getRounds()).isEqualTo(before);
        verify(host.getConnection()).sendMessage(isA(DenyChangeRoomMessage.class));
    }

    @Test
    @DisplayName("rounds above 30 is rejected and leaves settings unchanged")
    void changeRoomSettings_roundsAboveMax_denied() {
        int before = room.getSettings().getRounds();

        changeSettings(host, 8, 31, 30);

        assertThat(room.getSettings().getRounds()).isEqualTo(before);
        verify(host.getConnection()).sendMessage(isA(DenyChangeRoomMessage.class));
    }

    @Test
    @DisplayName("a non-host cannot change settings")
    void changeRoomSettings_byNonHost_denied() throws Exception {
        GameUser player = user("player");
        join(player);
        int before = room.getSettings().getRoomSize();

        changeSettings(player, 6, 20, 60);

        assertThat(room.getSettings().getRoomSize()).isEqualTo(before);
        verify(player.getConnection()).sendMessage(isA(DenyChangeRoomMessage.class));
    }

    // ---------------------------------------------------------------------
    // admin swap
    // ---------------------------------------------------------------------

    @Test
    @DisplayName("the host can hand the admin role to another member")
    void changeHost_reassignsToMember() throws Exception {
        GameUser player = user("player");
        join(player);

        room.changeHost(host, player.getUserUUID());
        flush();

        assertThat(room.getHost()).isSameAs(player);
    }

    @Test
    @DisplayName("a non-host cannot reassign the admin role")
    void changeHost_byNonHost_ignored() throws Exception {
        GameUser player = user("player");
        join(player);

        room.changeHost(player, host.getUserUUID());
        flush();

        assertThat(room.getHost()).isSameAs(host);
    }

    @Test
    @DisplayName("assigning admin to a non-member is ignored")
    void changeHost_targetNotInRoom_ignored() {
        room.changeHost(host, UUID.randomUUID());
        flush();

        assertThat(room.getHost()).isSameAs(host);
    }
}
