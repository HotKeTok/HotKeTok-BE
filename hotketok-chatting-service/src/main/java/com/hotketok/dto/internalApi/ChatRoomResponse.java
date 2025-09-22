package com.hotketok.dto.internalApi;

import com.hotketok.domain.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChatRoomResponse {
    private final Long roomId;
    private final String roomName;
    private final List<ParticipantResponse> participants;
    private final LocalDateTime createdAt;

    public ChatRoomResponse(ChatRoom chatRoom) {
        this.roomId = chatRoom.getId();
        this.roomName = chatRoom.getName();
        this.participants = chatRoom.getParticipants().stream()
                .map(ParticipantResponse::new)
                .collect(Collectors.toList());
        this.createdAt = chatRoom.getCreatedAt();
    }
}
