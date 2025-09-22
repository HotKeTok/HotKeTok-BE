package com.hotketok.repository;

import com.google.common.base.Optional;
import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);

    // 채팅방 목록 조회 마지막 메시지 1건을 가져옴
    Optional<ChatMessage> findTopByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom);
}