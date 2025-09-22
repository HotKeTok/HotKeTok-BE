package com.hotketok.domain;

import com.hotketok.domain.enums.SenderType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "participants")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ParticipantId.class) // 복합 키
@EntityListeners(AuditingEntityListener.class) // @CreatedDate 를 위함
public class Participant {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender_type", nullable = false)
    private SenderType senderType;

    @CreatedDate
    @Column(name = "joined_at", updatable = false, nullable = false)
    private LocalDateTime joinedAt;

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
