package com.hotketok.repository;

import com.hotketok.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByUserId(Long userId);

    // 유저가 속하는 채팅방이 맞는지 확인
    boolean existsByChatRoomIdAndUserId(Long chatRoomId, Long userId);

    // 읽음 여부 반영
    Optional<Participant> findByChatRoomIdAndUserId(Long chatRoomId, Long userId);
}