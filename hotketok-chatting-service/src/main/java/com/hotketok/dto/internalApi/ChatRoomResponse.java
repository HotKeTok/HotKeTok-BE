package com.hotketok.dto.internalApi;

import com.hotketok.domain.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ChatRoomResponse(
        Long roomId,
        String roomName,
        LocalDateTime createdAt,
        List<ParticipantResponse> participants
) {
    public ChatRoomResponse(ChatRoom chatRoom) {
        this(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getCreatedAt(),
                chatRoom.getParticipants().stream()
                        .map(ParticipantResponse::new)
                        .collect(Collectors.toList())
        );
    }
}