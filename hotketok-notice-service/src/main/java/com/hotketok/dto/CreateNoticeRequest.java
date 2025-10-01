package com.hotketok.dto;

public record CreateNoticeRequest(
        String title,
        String content,
        boolean isFix
) {
}