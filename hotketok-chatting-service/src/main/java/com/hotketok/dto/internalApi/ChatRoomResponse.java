package com.hotketok.dto.internalApi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import com.hotketok.domain.enums.ChatRoomType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatRoomResponse(
        Long roomId,
        String lastMessageContent,
        LocalDateTime lastMessageTime,
        long unreadCount,
        List<ParticipantResponse> participants,
        String address,
        String estimateStatus
) {

    public ChatRoomResponse(ChatRoom chatRoom, ChatMessage lastMessage, long unreadCount,
                            List<ParticipantResponse> participants,
                            String address, String estimateStatus) {
        this(
                chatRoom.getId(),
                lastMessage != null ? lastMessage.getContent() : "아직 메시지가 없습니다.",
                lastMessage != null ? lastMessage.getCreatedAt() : chatRoom.getCreatedAt(),
                unreadCount,
                participants,
                address,     
                estimateStatus
        );
    }
}