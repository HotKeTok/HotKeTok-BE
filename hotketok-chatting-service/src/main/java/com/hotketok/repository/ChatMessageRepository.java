package com.hotketok.repository;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import com.hotketok.domain.Participant;
import com.hotketok.dto.internalApi.UnreadCountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // 주어진 채팅방 목록의 마지막 메시지를 한 번의 쿼리로 조회
    @Query("SELECT cm FROM ChatMessage cm " +
            "WHERE cm.id IN " +
            "(SELECT MAX(cm2.id) FROM ChatMessage cm2 WHERE cm2.chatRoom IN :chatRooms GROUP BY cm2.chatRoom)")
    List<ChatMessage> findLatestMessagesForRooms(@Param("chatRooms") List<ChatRoom> chatRooms);

    // lastReadAt이 있는 참여자들의 안 읽은 메시지 수를 한 번의 쿼리로 조회
    @Query("SELECT new com.hotketok.dto.internalApi.UnreadCountDTO(p.id, COUNT(cm.id)) " +
            "FROM Participant p " +
            "LEFT JOIN p.chatRoom cr ON cr = p.chatRoom " +
            "LEFT JOIN ChatMessage cm ON cm.chatRoom = cr " + // 해당 채팅방의 메시지
            "WHERE p IN :participants " +
            "AND p.lastReadAt IS NOT NULL " + // 1. 읽은 적이 있고
            "AND cm.createdAt > p.lastReadAt " + // 2. 읽은 시각보다 최신이며
            "AND cm.senderId != :userId " + // 3. 내가 보낸게 아닌 메시지
            "GROUP BY p.id")
    List<UnreadCountDTO> getUnreadCountsForReadParticipants(
            @Param("participants") List<Participant> participants,
            @Param("userId") Long userId);

    // lastReadAt이 없는 참여자들의 안 읽은 메시지 수를 한 번의 쿼리로 조회
    @Query("SELECT new com.hotketok.dto.internalApi.UnreadCountDTO(p.id, COUNT(cm.id)) " +
            "FROM Participant p " +
            "LEFT JOIN p.chatRoom cr ON cr = p.chatRoom " +
            "LEFT JOIN ChatMessage cm ON cm.chatRoom = cr " +
            "WHERE p IN :participants " +
            "AND p.lastReadAt IS NULL " + // 1. 읽은 적이 없고
            "AND cm.senderId != :userId " + // 2. 내가 보낸게 아닌 메시지
            "GROUP BY p.id")
    List<UnreadCountDTO> getUnreadCountsForUnreadParticipants(
            @Param("participants") List<Participant> participants,
            @Param("userId") Long userId);
}