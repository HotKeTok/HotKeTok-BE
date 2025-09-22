package com.hotketok.dto.internalApi;

import java.util.List;

public record CreateChatRoomRequest(
        String roomName,
        List<Long> participantUserIds
) {}

