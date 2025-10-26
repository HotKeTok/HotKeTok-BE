package com.hotketok.domain;

import com.hotketok.domain.enums.ChatRoomType;
import com.hotketok.hotketokjpaservice.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @Column(nullable = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType roomType;

    @Column(nullable = true)
    private Long requestFormId;

    @Column(name = "is_active", nullable = false) // 채팅방 활성 여부 (삭제 시 false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> messages = new ArrayList<>();


    // 채팅방 생성 시 기본 이름 부여
    @Builder(access = AccessLevel.PRIVATE)
    private ChatRoom(String name, ChatRoomType roomType, Long requestFormId) {
        this.name = name;
        this.roomType = roomType;
        this.requestFormId = requestFormId;
    }

    public static ChatRoom createChatRoom(ChatRoomType roomType, Long requestFormId) {
        return ChatRoom.builder()
                .name("채팅방-" + UUID.randomUUID().toString().substring(0, 8))
                .roomType(roomType)
                .requestFormId(requestFormId)
                .build();
    }

    public void addParticipant(Participant participant) {
        this.participants.add(participant);
        participant.setChatRoom(this);
    }

    public void leaveRoom() {
        this.isActive = false;
    }
}