package com.hotketok.service;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import com.hotketok.domain.Participant;
import com.hotketok.domain.enums.SenderType;
import com.hotketok.dto.internalApi.*;
import com.hotketok.internalApi.HouseServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.internalApi.VendorServiceClient;
import com.hotketok.repository.ChatMessageRepository;
import com.hotketok.repository.ChatRoomRepository;
import com.hotketok.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final UserServiceClient userServiceClient;
    private final VendorServiceClient vendorServiceClient;
    private final HouseServiceClient houseServiceClient;

    // 채팅방 생성 요청
    @Transactional
    public Long createChatRoom(CreateChatRoomRequest request) {
        List<Long> userIds = request.participantUserIds();

        Map<Long, SenderType> userRoles = userServiceClient.getUserProfilesByIds(userIds).stream()
                .collect(Collectors.toMap(UserProfileResponse::userId, UserProfileResponse::role));

        // 채팅방 객체 먼저 생성 후 저장
        ChatRoom chatRoom = ChatRoom.createChatRoom();
        chatRoomRepository.save(chatRoom);

        // 조회한 역할 정보를 사용하여 참여자 목록 생성
        List<Participant> participants = userIds.stream()
                .map(userId -> {
                    // 역할 정보가 없을 경우 기본값(OWNER)을 사용
                    SenderType userRole = userRoles.getOrDefault(userId, SenderType.OWNER);
                    return Participant.createParticipant(chatRoom, userId, userRole);
                })
                .collect(Collectors.toList());
        participantRepository.saveAll(participants);

        return chatRoom.getId();
    }

    // 특정 유저의 채팅방 목록 조회
    public List<ChatRoomResponse> findChatRoomsByUserId(Long userId) {
        List<Participant> participants = participantRepository.findByUserId(userId);
        List<ChatRoom> chatRooms = participants.stream().map(Participant::getChatRoom).toList();

        List<Long> allUserIds = chatRooms.stream()
                .flatMap(room -> room.getParticipants().stream().map(Participant::getUserId))
                .distinct().toList();

        if (allUserIds.isEmpty()) {
            return List.of();
        }

        Map<Long, UserProfileResponse> userProfiles = userServiceClient.getUserProfilesByIds(allUserIds).stream()
                .collect(Collectors.toMap(UserProfileResponse::userId, profile -> profile));

        // 호수 반환 추가
        Map<Long, HouseUnitResponse> unitNumbers = houseServiceClient.getUnitNumbersByUserIds(allUserIds).stream()
                .collect(Collectors.toMap(HouseUnitResponse::userId, info -> info));

        // 공사업체의 경우 카테고리 반환 추가
        List<Long> vendorIds = userProfiles.values().stream()
                .filter(p -> "VENDOR".equals(p.role()))
                .map(UserProfileResponse::userId).toList();

        Map<Long, VendorCategoryResponse> vendorCategories = Map.of();
        if (!vendorIds.isEmpty()) {
            vendorCategories = vendorServiceClient.getVendorCategoriesByIds(vendorIds).stream()
                    .collect(Collectors.toMap(VendorCategoryResponse::vendorId, vc -> vc));
        }

        final Map<Long, VendorCategoryResponse> finalVendorCategories = vendorCategories;

        return participants.stream().map(participant -> {
            ChatRoom chatRoom = participant.getChatRoom();
            List<ParticipantResponse> detailedParticipants = chatRoom.getParticipants().stream().map(p -> {
                UserProfileResponse profile = userProfiles.get(p.getUserId());
                HouseUnitResponse unit = unitNumbers.get(p.getUserId());
                VendorCategoryResponse category = finalVendorCategories.get(p.getUserId());

                return new ParticipantResponse(
                        p.getUserId(),
                        profile != null ? profile.userName() : "알 수 없는 사용자",
                        profile != null ? profile.profileImageUrl() : null,
                        p.getSenderType(), p.getJoinedAt(),
                        unit != null ? unit.unitNumber() : null,
                        category != null ? category.category() : null
                );
            }).collect(Collectors.toList());

            Optional<ChatMessage> lastMsgOpt = chatMessageRepository.findTopByChatRoomOrderByCreatedAtDesc(chatRoom);
            long unread = calculateUnreadCount(participant, chatRoom, userId);
            return new ChatRoomResponse(chatRoom, lastMsgOpt.orElse(null), unread, detailedParticipants);
        }).collect(Collectors.toList());
    }

    private long calculateUnreadCount(Participant p, ChatRoom cr, Long userId) {
        return (p.getLastReadAt() == null)
                ? chatMessageRepository.countByChatRoomAndSenderIdNot(cr, userId)
                : chatMessageRepository.countByChatRoomAndCreatedAtAfterAndSenderIdNot(cr, p.getLastReadAt(), userId);
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
    public ChatRoomDetailResponse findMessagesByRoomId(Long userId, Long roomId) {
        boolean isParticipant = participantRepository.existsByChatRoomIdAndUserId(roomId, userId);
        if (!isParticipant) {
            throw new SecurityException("해당 채팅방에 접근할 권한이 없습니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채팅방입니다. ID: " + roomId));

        // 참여자 정보 반환 추가
        List<ChatParticipantResponse> participants = getDetailedParticipants(chatRoom);

        // 메시지 반환
        List<ChatMessageResponse> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId).stream()
                .map(ChatMessageResponse::new)
                .collect(Collectors.toList());

        return new ChatRoomDetailResponse(participants, messages);
    }

    // 유저 정보 반환 함수 분리
    private List<ChatParticipantResponse> getDetailedParticipants(ChatRoom chatRoom) {
        List<Long> userIds = chatRoom.getParticipants().stream()
                .map(Participant::getUserId)
                .distinct().toList();

        if (userIds.isEmpty()) {
            return List.of();
        }

        Map<Long, UserProfileResponse> userProfiles = userServiceClient.getUserProfilesByIds(userIds).stream()
                .collect(Collectors.toMap(UserProfileResponse::userId, profile -> profile));

        return chatRoom.getParticipants().stream().map(p -> {
            UserProfileResponse profile = userProfiles.get(p.getUserId());

            return new ChatParticipantResponse(
                    p.getUserId(),
                    profile != null ? profile.userName() : "알 수 없는 사용자",
                    profile != null ? profile.profileImageUrl() : null
            );
        }).collect(Collectors.toList());
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