package com.hotketok.dto.internalApi;

import java.util.List;

public record ChatRoomDetailResponse(
        List<ChatParticipantResponse> participants,
        List<ChatMessageResponse> messages
) {}