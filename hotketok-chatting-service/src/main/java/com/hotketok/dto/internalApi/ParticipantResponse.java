package com.hotketok.dto.internalApi;

import com.hotketok.domain.Participant;
import com.hotketok.domain.enums.SenderType;
import com.hotketok.domain.enums.Status;
import com.hotketok.dto.internalApi.UserProfileResponse;

import java.time.LocalDateTime;

public record ParticipantResponse(
        Long userId,
        String userName,
        String profileImageUrl,
        SenderType senderType,
        LocalDateTime joinedAt,
        String address,
        Status status
) {
    // 시공업체 미포함 채팅방
    public ParticipantResponse(Participant participant, UserProfileResponse userProfile) {
        this(
                participant.getUserId(),
                userProfile != null ? userProfile.userName() : "알 수 없는 사용자",
                userProfile != null ? userProfile.profileImageUrl() : null,
                participant.getSenderType(),
                participant.getJoinedAt(),
                null, // address
                null  // estimateStatus
        );
    }

    // 시공업체 포함 채팅방
    public ParticipantResponse(Participant participant, UserProfileResponse userProfile,
                               RequestFormAddressStatusResponse formData) {
        this(
                participant.getUserId(),
                userProfile != null ? userProfile.userName() : "알 수 없는 사용자",
                userProfile != null ? userProfile.profileImageUrl() : null,
                participant.getSenderType(),
                participant.getJoinedAt(),
                formData != null ? formData.address() : null,
                formData != null ? formData.status() : null
        );
    }
}