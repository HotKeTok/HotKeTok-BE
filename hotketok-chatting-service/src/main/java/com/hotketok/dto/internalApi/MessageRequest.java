package com.hotketok.dto.internalApi;

public record MessageRequest(
        Long roomId,
        Long senderId,
        String content
) {}

