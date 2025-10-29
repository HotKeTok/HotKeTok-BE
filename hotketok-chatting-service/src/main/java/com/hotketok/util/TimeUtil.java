package com.hotketok.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class TimeUtil {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String formatTimeAgo(LocalDateTime pastTime) {
        if (pastTime == null) {
            return "";
        }

        Duration duration = Duration.between(pastTime, LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "방금 전";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return minutes + "분 전";
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return hours + "시간 전";
        } else if (seconds < 2592000) { // 30일 이내
            long days = seconds / 86400;
            return days + "일 전";
        } else {
            return pastTime.format(DATE_TIME_FORMATTER);
        }
    }
}