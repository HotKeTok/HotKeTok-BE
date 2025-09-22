package com.hotketok.dto.internalApi;

import com.hotketok.domain.ChatMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponse {
    private final Long messageId;
    private final Long roomId;
    private final Long senderId;
    private final String content;
    private final LocalDateTime createdAt;

    public ChatMessageResponse(ChatMessage chatMessage) {
        this.messageId = chatMessage.getId();
        this.roomId = chatMessage.getChatRoom().getId();
        this.senderId = chatMessage.getSenderId();
        this.content = chatMessage.getContent();
        this.createdAt = chatMessage.getCreatedAt();
    }
}
