package com.semmelzahntiger.brainrotbackend.util;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class LobbyCodeGenerator {

    private static final Set<String> EXISTING_CODES = ConcurrentHashMap.newKeySet();

    private static final List<Character> PARTS = List.of(
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R',
            'S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9');

    public static String generateCode() {
        String lobbyCode;
        do {
            lobbyCode = randomCode();
        } while (!EXISTING_CODES.add(lobbyCode));
        return lobbyCode;
    }

    private static String randomCode() {
        StringBuilder code = new StringBuilder(6);
        int range = PARTS.size();
        for (int i = 0; i < 6; i++) {
            code.append(PARTS.get(ThreadLocalRandom.current().nextInt(range)));
        }
        return code.toString();
    }

    public static void freeLobbyCode(String code) {
        EXISTING_CODES.remove(code); // already thread-safe via ConcurrentHashMap.newKeySet()
    }

    public static boolean alreadyExists(String code) {
        return EXISTING_CODES.contains(code); // fine to keep as a separate read-only check elsewhere
    }
}
