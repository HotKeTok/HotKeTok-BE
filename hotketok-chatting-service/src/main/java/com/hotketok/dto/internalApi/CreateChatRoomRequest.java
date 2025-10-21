package com.hotketok.dto.internalApi;

import com.hotketok.domain.enums.ChatRoomType;

import java.util.List;

public record CreateChatRoomRequest(
        List<Long> participantUserIds,
        ChatRoomType roomType,
        Long requestFormId // roomType이 GENERAL이면 null
) {}

