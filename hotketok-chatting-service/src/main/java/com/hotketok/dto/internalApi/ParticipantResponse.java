package com.hotketok.dto.internalApi;

import com.hotketok.domain.Participant;
import com.hotketok.domain.enums.Category;
import com.hotketok.domain.enums.SenderType;
import com.hotketok.domain.enums.Status;

import java.time.LocalDateTime;

public record ParticipantResponse(
        Long userId,
        String userName,
        String profileImageUrl,
        SenderType senderType,
        LocalDateTime joinedAt,
        String unitNumber, // TENANT일 경우 호수 정보
        Category category // VENDOR일 경우 카테고리
) {
}