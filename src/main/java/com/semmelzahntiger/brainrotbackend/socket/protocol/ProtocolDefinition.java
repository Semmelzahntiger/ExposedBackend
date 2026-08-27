package com.semmelzahntiger.brainrotbackend.socket.protocol;

public class ProtocolDefinition {
    // Incoming Message Definition
    public static final String INCOMING_AUTHENTICATION_MESSAGE = "send_authentication";
    public static final String INCOMING_CREATE_ROOM_MESSAGE = "create_room";
    public static final String INCOMING_JOIN_ROOM_MESSAGE = "join_room";
    public static final String INCOMING_LEAVE_ROOM_MESSAGE = "leave_room";
    public static final String INCOMING_CHANGE_ROOM_SETTINGS = "change_room_settings";
    public static final String INCOMING_START_GAME_MESSAGE = "start_game";
    public static final String INCOMING_RETURN_TO_LOBBY_MESSAGE = "return_to_lobby";
    public static final String INCOMING_CHANGE_ROLE_MESSAGE = "change_role";
    public static final String INCOMING_SUBMIT_GUESS_MESSAGE = "submit_guess";
    public static final String INCOMING_SUBMIT_MULTI_GUESS_MESSAGE = "submit_multi_guess";
    public static final String INCOMING_CHANGE_LOBBY_ADMIN_MESSAGE = "change_lobby_admin";

    // Outgoing Message Definition
    public static final String CONFIRM_AUTHENTICATION_MESSAGE = "confirm_authentication";
    public static final String DENY_AUTHENTICATION_MESSAGE = "denied_authentication";
    public static final String CONFIRM_JOIN_ROOM_MESSAGE = "confirm_join_room";
    public static final String DENY_JOIN_ROOM_MESSAGE = "denied_join_room";
    public static final String CONFIRM_CHANGE_ROOM_SETTINGS = "confirm_change_room_settings";
    public static final String DENY_CHANGE_ROOM_MESSAGE = "denied_change_room_settings";
    public static final String STARTED_GAME_MESSAGE = "started_game";
    public static final String DENY_START_GAME = "denied_start_game";
    public static final String CONFIRM_RETURN_TO_LOBBY_MESSAGE = "confirm_return_to_lobby";
    public static final String DENY_RETURN_TO_LOBBY_MESSAGE = "denied_return_to_lobby";
    public static final String UPDATE_ROOM_STATE_MESSAGE = "update_room_state";
    public static final String NEXT_ROUND_MESSAGE = "next_round";
    public static final String GUESS_RESULT_MESSAGE = "guess_result";
    public static final String MULTI_GUESS_RESULT_MESSAGE = "multi_guess_result";
    public static final String GAME_SCORE_STATE_MESSAGE = "game_score_state";
    public static final String GAME_OVER_MESSAGE = "game_over";
    public static final String ROOM_NOT_FOUND_MESSAGE = "room_not_found";

    public static final String CONFIRM_CREATE_ROOM_MESSAGE = "confirm_create_room";
    public static final String DENY_CREATE_ROOM_MESSAGE = "denied_create_room";
    public static final String CONFIRM_LEAVE_ROOM_MESSAGE = "confirm_leave_room";

}
