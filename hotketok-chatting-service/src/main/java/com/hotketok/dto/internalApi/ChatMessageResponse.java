package com.hotketok.dto.internalApi;

import com.hotketok.domain.ChatMessage;
import com.hotketok.util.TimeUtil;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long roomId,
        Long senderId,
        String content,
        LocalDateTime createdAt,
        String displayTime
) {
    public ChatMessageResponse(ChatMessage chatMessage) {
        this(
                chatMessage.getId(),
                chatMessage.getChatRoom().getId(),
                chatMessage.getSenderId(),
                chatMessage.getContent(),
                chatMessage.getCreatedAt(),
                TimeUtil.formatTimeAgo(chatMessage.getCreatedAt())
        );
    }
}
