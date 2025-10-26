package com.hotketok.service;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import com.hotketok.domain.Participant;
import com.hotketok.domain.enums.ChatRoomType;
import com.hotketok.domain.enums.SenderType;
import com.hotketok.dto.internalApi.*;
import com.hotketok.exception.ChattingErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.RequestFormServiceClient;
import com.hotketok.internalApi.HouseServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.internalApi.VendorServiceClient;
import com.hotketok.repository.ChatMessageRepository;
import com.hotketok.repository.ChatRoomRepository;
import com.hotketok.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    private final VendorServiceClient vendorServiceClient;
    private final HouseServiceClient houseServiceClient;

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
        // 1. 참여자 정보 + 채팅방 정보 + 채팅방별 참여자 목록 한 번에 조회
        List<Participant> participants = participantRepository.findByUserIdWithDetails(userId);
        if (participants.isEmpty()) {
            return List.of();
        }

        List<ChatRoom> chatRooms = participants.stream().map(Participant::getChatRoom).toList();

        // 2. 채팅방별 마지막 메시지를 Map으로 한 번에 조회
        Map<Long, ChatMessage> lastMessageMap = chatMessageRepository.findLatestMessagesForRooms(chatRooms).stream()
                .collect(Collectors.toMap(msg -> msg.getChatRoom().getId(), Function.identity()));

        // 3. 안 읽은 메시지 수를 한 번에 조회 (두 쿼리 결과를 합침)
        Map<Long, Long> unreadCountMap = new HashMap<>();

        chatMessageRepository.getUnreadCountsForReadParticipants(participants, userId)
                .forEach(dto -> unreadCountMap.put(dto.participantId(), dto.count()));

        chatMessageRepository.getUnreadCountsForUnreadParticipants(participants, userId)
                .forEach(dto -> unreadCountMap.put(dto.participantId(), dto.count()));

        // 주소, 상태 반환
        List<Long> requestFormIds = chatRooms.stream()
                .filter(room -> room.getRoomType() == ChatRoomType.VENDOR_ESTIMATE)
                .map(ChatRoom::getRequestFormId)
                .distinct().toList();

        Map<Long, RequestFormAddressStatusResponse> requestFormMap = Collections.emptyMap();
        if (!requestFormIds.isEmpty()) {
            log.info(">>> Calling requestform-service with requestFormIds: {}", requestFormIds);
            requestFormMap = requestFormServiceClient.getRequestFormsAddressAndStatus(requestFormIds).stream()
                    .collect(Collectors.toMap(RequestFormAddressStatusResponse::requestFormId, data -> data));
            log.info("<<< Received requestFormMap from requestform-service: {}", requestFormMap);
        }
        final Map<Long, RequestFormAddressStatusResponse> finalRequestFormMap = requestFormMap;

        List<Long> allUserIds = chatRooms.stream()
                .flatMap(room -> room.getParticipants().stream().map(Participant::getUserId))
                .distinct().toList();

        if (allUserIds.isEmpty()) {
            return List.of();
        }

        Map<Long, UserProfileResponse> userProfiles = userServiceClient.getUserProfilesByIds(allUserIds).stream()
                .collect(Collectors.toMap(UserProfileResponse::userId, profile -> profile));

        Map<Long, HouseUnitResponse> unitNumbers = houseServiceClient.getUnitNumbersByUserIds(allUserIds).stream()
                .collect(Collectors.toMap(HouseUnitResponse::userId, info -> info));

        // 공사업체의 경우 카테고리 반환 추가
        List<Long> vendorIds = userProfiles.values().stream()
                .filter(p -> p.role() == SenderType.VENDOR)
                .map(UserProfileResponse::userId).toList();

        Map<Long, VendorCategoryResponse> vendorCategories = Map.of();
        if (!vendorIds.isEmpty()) {
            vendorCategories = vendorServiceClient.getVendorCategoriesByIds(vendorIds).stream()
                    .collect(Collectors.toMap(VendorCategoryResponse::vendorId, vc -> vc));
        }
        final Map<Long, VendorCategoryResponse> finalVendorCategories = vendorCategories;

        return participants.stream().map(participant -> {
            ChatRoom chatRoom = participant.getChatRoom();

            // 상세 참여자 목록 생성
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

            // 마지막 메시지 조회
            ChatMessage lastMsg = lastMessageMap.get(chatRoom.getId());

            // 안 읽은 수 조회
            long unread = unreadCountMap.getOrDefault(participant.getId(), 0L);

            // 주소 및 상태값 추출
            String address = null;
            String estimateStatus = null;
            if (chatRoom.getRoomType() == ChatRoomType.VENDOR_ESTIMATE && finalRequestFormMap != null) {
                RequestFormAddressStatusResponse formData = finalRequestFormMap.get(chatRoom.getRequestFormId());
                if (formData != null) {
                    address = formData.address();
                    estimateStatus = formData.status().name();
                }
            }

            return new ChatRoomResponse(chatRoom, lastMsg, unread, detailedParticipants, address, estimateStatus);
        }).collect(Collectors.toList());
    }

    // 채팅방 삭제
    @Transactional
    public void deleteChatRoom(Long userId, Long roomId) {

        // 유저가 해당 채팅방의 참여자인지 확인
        boolean isParticipant = participantRepository.existsByChatRoomIdAndUserId(roomId, userId);
        if (!isParticipant)
            throw new CustomException(ChattingErrorCode.NOT_A_PARTICIPANT);
        chatRoomRepository.deleteById(roomId);
    }

    // 채팅 내용 조회
    @Transactional
    public ChatRoomDetailResponse findMessagesByRoomId(Long userId, Long roomId) {

        Participant participant = participantRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new CustomException(ChattingErrorCode.NOT_A_PARTICIPANT));

        // 읽음 여부 갱신
        participant.updateLastReadAt(LocalDateTime.now());

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND));
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
                .orElseThrow(() -> new CustomException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatMessage chatMessage = ChatMessage.createChatMessage(chatRoom, request.senderId(), request.content());
        return chatMessageRepository.save(chatMessage);
    }
}