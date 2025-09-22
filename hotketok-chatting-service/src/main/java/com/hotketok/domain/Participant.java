package com.hotketok.domain;

import com.hotketok.domain.enums.SenderType;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ParticipantId.class)
public class Participant extends BaseTimeEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType senderType;

    @Builder
    private Participant(ChatRoom chatRoom, Long userId, SenderType senderType) {
        this.chatRoom = chatRoom;
        this.userId = userId;
        this.senderType = senderType;
    }

    public static Participant createParticipant(ChatRoom chatRoom, Long userId, SenderType senderType) {
        return Participant.builder()
                .chatRoom(chatRoom)
                .userId(userId)
                .senderType(senderType)
                .build();
    }
}