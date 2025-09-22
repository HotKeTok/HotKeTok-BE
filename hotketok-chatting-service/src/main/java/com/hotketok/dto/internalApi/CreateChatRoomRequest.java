package com.hotketok.dto.internalApi;

import java.util.List;

public record CreateChatRoomRequest(
        List<Long> participantUserIds
) {}

