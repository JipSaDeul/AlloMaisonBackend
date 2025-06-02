package com.example.allomaison.Utils;

import java.util.UUID;

public class UUIDUtil {

    public static long uuidToLong() {

        UUID uuid = UUID.randomUUID();

        return uuid.getMostSignificantBits() ^ uuid.getLeastSignificantBits();

    }

}