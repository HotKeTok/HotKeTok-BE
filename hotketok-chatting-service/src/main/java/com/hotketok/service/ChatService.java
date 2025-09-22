package com.hotketok.service;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import com.hotketok.domain.Participant;
import com.hotketok.domain.enums.SenderType;
import com.hotketok.dto.internalApi.ChatMessageResponse;
import com.hotketok.dto.internalApi.ChatRoomResponse;
import com.hotketok.dto.internalApi.CreateChatRoomRequest;
import com.hotketok.dto.internalApi.UserProfileResponse;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.ChatMessageRepository;
import com.hotketok.repository.ChatRoomRepository;
import com.hotketok.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final UserServiceClient userServiceClient;

    // 채팅방 생성 요청
    @Transactional
    public Long createChatRoom(CreateChatRoomRequest request) {
        List<Long> userIds = request.participantUserIds();

        Map<Long, SenderType> userRoles = userServiceClient.getUserRolesByIds(userIds).stream()
                .collect(Collectors.toMap(UserProfileResponse::userId, UserProfileResponse::role));

        // 채팅방 객체 먼저 생성 후 저장
        ChatRoom chatRoom = ChatRoom.createChatRoom();
        chatRoomRepository.save(chatRoom);

        // 조회한 역할 정보를 사용하여 참여자 목록 생성
        List<Participant> participants = userIds.stream()
                .map(userId -> {
                    // 역할 정보가 없을 경우 기본값(HOUSE_USER)을 사용
                    SenderType userRole = userRoles.getOrDefault(userId, SenderType.HOUSE_USER);
                    return Participant.createParticipant(chatRoom, userId, userRole);
                })
                .collect(Collectors.toList());
        participantRepository.saveAll(participants);

        return chatRoom.getId();
    }

    // 특정 유저의 채팅방 목록 조회
    public List<ChatRoomResponse> findChatRoomsByUserId(Long userId) {
        List<ChatRoom> chatRooms = participantRepository.findByUserId(userId).stream()
                .map(Participant::getChatRoom)
                .toList();

        return chatRooms.stream()
                .map(chatRoom -> {
                    // 마지막 메시지 조회
                    Optional<ChatMessage> lastMessageOpt = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom).toJavaUtil();
                    ChatMessage lastMessage = lastMessageOpt.orElse(null);

                    // 안 읽은 메시지 수 계산
                    long unreadCount = 0; // 지금은 임시로 0으로 설정

                    return new ChatRoomResponse(chatRoom, lastMessage, unreadCount, userId);
                })
                .collect(Collectors.toList());
    }

public List<ChatMessageResponse> findMessagesByRoomId(Long roomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId).stream()
                .map(ChatMessageResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + roomId));

        ChatMessage chatMessage = ChatMessage.createChatMessage(chatRoom, senderId, content);
        return chatMessageRepository.save(chatMessage);
    }
}