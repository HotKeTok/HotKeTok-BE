package com.hotketok.dto.internalApi;

import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ChatRoomResponse(
        Long roomId,
        String lastMessageContent,
        LocalDateTime lastMessageTime,
        long unreadCount,
        List<ParticipantResponse> participants
) {

    public ChatRoomResponse(ChatRoom chatRoom, ChatMessage lastMessage, long unreadCount,
                            Map<Long, UserProfileResponse> userProfiles,
                            Map<Long, RequestFormDataResponse> requestFormMap) {
        this(
                chatRoom.getId(),
                lastMessage != null ? lastMessage.getContent() : "아직 메시지가 없습니다.",
                lastMessage != null ? lastMessage.getCreatedAt() : chatRoom.getCreatedAt(),
                unreadCount,
                chatRoom.getParticipants().stream()
                        .map(participant -> {
                            UserProfileResponse userProfile = userProfiles.get(participant.getUserId());
                            // 업체 포함된 채팅방일 경우 추가 정보를 사용
                            if (chatRoom.getRoomType() == ChatRoomType.VENDOR_ESTIMATE) {
                                RequestFormAddressStatusResponse formData = (requestFormMap != null) ? requestFormMap.get(chatRoom.getRequestFormId()) : null;
                                return new ParticipantResponse(participant, userProfile, formData);
                            } else {
                                // 일반 채팅방일 경우 기존 생성자 사용
                                return new ParticipantResponse(participant, userProfile);
                            }
                        })
                        .collect(Collectors.toList())
        );
    }
}