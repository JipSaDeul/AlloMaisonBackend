package com.example.allomaison.Utils;

import java.util.UUID;

public class UUIDUtil {
    private static final long JS_SAFE_MAX = 9007199254740991L; // 2^53 - 1

    public static long uuidToLong() {

        UUID uuid = UUID.randomUUID();
        long raw = Math.abs(uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits());
        return raw % JS_SAFE_MAX;

    }

}