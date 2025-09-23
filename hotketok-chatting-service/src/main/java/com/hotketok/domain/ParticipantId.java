package com.hotketok.domain;

import java.io.Serializable;
import java.util.Objects;

// JPA 복합 키를 위한 식별자 클래스
public class ParticipantId implements Serializable {

    private Long chatRoom;
    private Long userId;

    // 기본 생성자
    public ParticipantId() {
    }

    // 기본적인 equals, hashCode 메서드
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParticipantId that = (ParticipantId) o;
        return Objects.equals(chatRoom, that.chatRoom) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatRoom, userId);
    }
}