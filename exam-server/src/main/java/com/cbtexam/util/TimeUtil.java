package com.cbtexam.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class TimeUtil {

    private TimeUtil() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static long secondsRemaining(LocalDateTime serverEndTime) {
        long seconds = ChronoUnit.SECONDS.between(now(), serverEndTime);
        return Math.max(seconds, 0);
    }

    public static boolean isExpired(LocalDateTime serverEndTime) {
        return !now().isBefore(serverEndTime);
    }
}
