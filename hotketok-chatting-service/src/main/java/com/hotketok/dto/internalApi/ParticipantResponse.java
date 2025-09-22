package com.hotketok.dto.internalApi;

import com.hotketok.domain.Participant;
import com.hotketok.domain.enums.SenderType;

import java.time.LocalDateTime;

public record ParticipantResponse(
        Long userId,
        SenderType senderType,
        LocalDateTime joinedAt
) {
    public ParticipantResponse(Participant participant) {
        this(
                participant.getUserId(),
                participant.getSenderType(),
                participant.getJoinedAt()
        );
    }
}
