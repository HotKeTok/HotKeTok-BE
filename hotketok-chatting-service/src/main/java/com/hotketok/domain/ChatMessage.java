package com.hotketok.domain;

import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chatroom_created_at", columnList = "chatroom_id, createdAt") // 인덱스 설정 추가
})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public record ChatMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatmessage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(length = 1000) // 메시지 내용 길이 1000 제한
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private ChatMessage(ChatRoom chatRoom, Long senderId, String content) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
        this.content = content;
    }

    public static ChatMessage createChatMessage(ChatRoom chatRoom, Long senderId, String content) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderId(senderId)
                .content(content)
                .build();
    }
}
