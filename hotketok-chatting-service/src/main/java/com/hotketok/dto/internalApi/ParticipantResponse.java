package com.hotketok.dto.internalApi;

import com.hotketok.domain.Participant;
import lombok.Getter;

@Getter
public class ParticipantResponse {
    private final Long userId;
    private final String userType;

    public ParticipantResponse(Participant participant) {
        this.userId = participant.getUserId();
        this.userType = participant.getSenderType().name();
    }
}
