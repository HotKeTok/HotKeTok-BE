package com.hotketok.service;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import com.hotketok.domain.Participant;
import com.hotketok.domain.enums.ChatRoomType;
import com.hotketok.domain.enums.SenderType;
import com.hotketok.dto.internalApi.*;
import com.hotketok.internalApi.RequestFormServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.ChatMessageRepository;
import com.hotketok.repository.ChatRoomRepository;
import com.hotketok.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final UserServiceClient userServiceClient;
    private final RequestFormServiceClient requestFormServiceClient;

    // 채팅방 생성 요청
    @Transactional
    public Long createChatRoom(CreateChatRoomRequest request) {
        List<Long> userIds = request.participantUserIds();
        ChatRoomType roomType = request.roomType();

        ChatRoom chatRoom = ChatRoom.createChatRoom(request.roomType(), request.requestFormId());

        Map<Long, SenderType> userRoles = userServiceClient.getUserProfilesByIds(userIds).stream()
                .collect(Collectors.toMap(UserProfileResponse::userId, UserProfileResponse::role));

        userIds.forEach(userId -> {
            SenderType userRole = userRoles.getOrDefault(userId, SenderType.OWNER);
            Participant participant = Participant.createParticipant(chatRoom, userId, userRole);
            chatRoom.addParticipant(participant);
        });
        chatRoomRepository.save(chatRoom);
        return chatRoom.getId();
    }

    // 특정 유저의 채팅방 목록 조회
    public List<ChatRoomResponse> findChatRoomsByUserId(Long userId) {
        List<Participant> participants = participantRepository.findByUserId(userId);
        List<ChatRoom> chatRooms = participants.stream().map(Participant::getChatRoom).toList();

        // 함께톡인 것들만 목록
        List<Long> requestFormIds = chatRooms.stream()
                .filter(room -> room.getRoomType() == ChatRoomType.VENDOR_ESTIMATE)
                .map(ChatRoom::getRequestFormId)
                .distinct().toList();

        // address, status 반환 추가
        Map<Long, RequestFormAddressStatusResponse> requestFormMap = Collections.emptyMap();
        if (!requestFormIds.isEmpty()) {
            log.info(">>> Calling requestform-service with requestFormIds: {}", requestFormIds);
            requestFormMap = requestFormServiceClient.getRequestFormsAddressAndStatus(requestFormIds).stream()
                    .collect(Collectors.toMap(RequestFormAddressStatusResponse::requestFormId, data -> data));
            log.info("<<< Received requestFormMap from requestform-service: {}", requestFormMap);
        }

        List<Long> allParticipantIds = chatRooms.stream()
                .flatMap(room -> room.getParticipants().stream())
                .map(Participant::getUserId)
                .distinct() // 중복 제거 포함
                .toList();

        Map<Long, UserProfileResponse> userProfiles = userServiceClient.getUserProfilesByIds(allParticipantIds).stream()
                .collect(Collectors.toMap(UserProfileResponse::userId, profile -> profile));

        // 각 채팅방에 대해 안 읽은 메시지 수 계산
        final Map<Long, RequestFormAddressStatusResponse> finalRequestFormMap = requestFormMap;
        return participants.stream()
                .map(participant -> {
                    ChatRoom chatRoom = participant.getChatRoom();

                    // 마지막 메시지를 조회
                    Optional<ChatMessage> lastMessageOpt = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom);
                    ChatMessage lastMessage = lastMessageOpt.orElse(null);

                    // 안 읽은 메시지 수
                    LocalDateTime lastReadAt = participant.getLastReadAt();
                    long unreadCount;
                    if (lastReadAt == null) {
                        unreadCount = chatMessageRepository.countByChatRoomAndSenderIdNot(chatRoom, userId);
                    } else {
                        // 마지막으로 읽은 시간 이후에 온, 내가 보내지 않은 메시지의 수 카운드
                        unreadCount = chatMessageRepository.countByChatRoomAndCreatedAtAfterAndSenderIdNot(chatRoom, lastReadAt, userId);
                    }

                    return new ChatRoomResponse(chatRoom, lastMessage, unreadCount, userProfiles, finalRequestFormMap);
                })
                .collect(Collectors.toList());
    }

    // 채팅방 삭제
    @Transactional
    public void deleteChatRoom(Long userId, Long roomId) {

        // 유저가 해당 채팅방의 참여자인지 확인
        boolean isParticipant = participantRepository.existsByChatRoomIdAndUserId(roomId, userId);
        if (!isParticipant) {
            throw new IllegalArgumentException("사용자가 해당 채팅방에 참여하고 있지 않으므로 삭제할 권한이 없습니다.");
        }
        chatRoomRepository.deleteById(roomId);
    }

    // 채팅 내용 조회
    public List<ChatMessageResponse> findMessagesByRoomId(Long userId, Long roomId) {
        boolean isParticipant = participantRepository.existsByChatRoomIdAndUserId(roomId, userId);
        if (!isParticipant) {
            throw new SecurityException("해당 채팅방에 접근할 권한이 없습니다.");
        }
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId).stream()
                .map(ChatMessageResponse::new)
                .collect(Collectors.toList());
    }

    // 채팅을 보냈을 때 저장
    @Transactional
    public ChatMessage saveMessage(MessageRequest request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.roomId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + request.roomId()));

        ChatMessage chatMessage = ChatMessage.createChatMessage(chatRoom, request.senderId(), request.content());
        return chatMessageRepository.save(chatMessage);
    }
}