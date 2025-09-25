package com.hotketok.dto.internalApi;

import com.hotketok.domain.Participant;
import com.hotketok.domain.enums.SenderType;
import com.hotketok.dto.internalApi.UserProfileResponse;

import java.time.LocalDateTime;

public record ParticipantResponse(
        Long userId,
        String userName,
        String profileImageUrl,
        SenderType senderType,
        LocalDateTime joinedAt
) {
    public ParticipantResponse(Participant participant, UserProfileResponse userProfile) {
        this(
                participant.getUserId(),
                userProfile != null ? userProfile.userName() : "알 수 없는 사용자",
                userProfile != null ? userProfile.profileImageUrl() : null,
                participant.getSenderType(),
                participant.getJoinedAt()
        );
    }
}