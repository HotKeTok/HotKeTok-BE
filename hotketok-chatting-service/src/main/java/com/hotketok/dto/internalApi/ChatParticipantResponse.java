package com.hotketok.dto.internalApi;

public record ChatParticipantResponse(
        Long userId,
        String userName,
        String profileImageUrl
) {}