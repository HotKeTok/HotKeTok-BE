package com.hotketok.dto.internalApi;

public record MessageRequest(
        Long roomId,
        String content
) {}
