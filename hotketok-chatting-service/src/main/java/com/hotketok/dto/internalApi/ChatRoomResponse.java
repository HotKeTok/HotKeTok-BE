package com.hotketok.dto.internalApi;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ChatRoomResponse(
        Long roomId,
        String lastMessageContent,
        LocalDateTime lastMessageTime,
        long unreadCount,
        List<ParticipantResponse> participants
) {

    public ChatRoomResponse(ChatRoom chatRoom, ChatMessage lastMessage, long unreadCount, Map<Long, UserProfileResponse> userProfiles) {
        this(
                chatRoom.getId(),
                lastMessage != null ? lastMessage.getContent() : "아직 메시지가 없습니다.",
                lastMessage != null ? lastMessage.getCreatedAt() : chatRoom.getCreatedAt(),
                unreadCount,
                chatRoom.getParticipants().stream()
                        .map(participant -> {
                            UserProfileResponse userProfile = userProfiles.get(participant.getUserId());
                            return new ParticipantResponse(participant, userProfile);
                        })
                        .collect(Collectors.toList())
        );
    }
}