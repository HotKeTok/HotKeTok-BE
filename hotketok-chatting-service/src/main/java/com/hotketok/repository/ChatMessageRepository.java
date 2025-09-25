package com.hotketok.repository;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // 채팅방 목록 조회 마지막 메시지 1건을 가져옴
    Optional<ChatMessage> findTopByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);

    // 특정 시간 이후에 생성된, 안 읽은 메시지의 개수 카운트
    long countByChatRoomAndCreatedAtAfterAndSenderIdNot(ChatRoom chatRoom, LocalDateTime timestamp, Long userId);

    // 한 번도 읽지 않은 방의 안 읽은 메시지 카운트 (내가 보낸 메시지 제외)
    long countByChatRoomAndSenderIdNot(ChatRoom chatRoom, Long userId);
}