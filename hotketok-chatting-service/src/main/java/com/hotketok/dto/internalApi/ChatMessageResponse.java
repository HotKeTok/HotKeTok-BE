package com.hotketok.dto.internalApi;

import com.hotketok.domain.ChatMessage;
import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {
    public ChatMessageResponse(ChatMessage chatMessage) {
        this(
                chatMessage.getId(),
                chatMessage.getSenderId(),
                chatMessage.getContent(),
                chatMessage.getCreatedAt()
        );
    }
}
