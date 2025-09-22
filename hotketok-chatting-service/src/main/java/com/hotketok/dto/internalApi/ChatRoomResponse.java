package com.hotketok.dto.internalApi;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;

public record ChatRoomResponse(
        Long roomId,
        String lastMessageContent,
        LocalDateTime lastMessageTime,
        long unreadCount,
        List<ParticipantResponse> participants
) {

    public ChatRoomResponse(ChatRoom chatRoom, ChatMessage lastMessage, long unreadCount, Long currentUserId) {
        this(
                chatRoom.getId(),
                lastMessage != null ? lastMessage.getContent() : "아직 메시지가 없습니다.",
                lastMessage != null ? lastMessage.getCreatedAt() : chatRoom.getCreatedAt(),
                unreadCount,
                chatRoom.getParticipants().stream()
                        // 채팅방 이름 등을 표시하기 위해 나를 제외한 다른 참여자 목록을 반환
                        .filter(p -> !p.getUserId().equals(currentUserId))
                        .map(ParticipantResponse::new)
                        .toList()
        );
    }
}