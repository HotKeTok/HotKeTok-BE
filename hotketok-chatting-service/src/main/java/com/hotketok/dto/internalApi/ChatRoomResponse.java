package com.hotketok.dto.internalApi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hotketok.domain.ChatMessage;
import com.hotketok.domain.ChatRoom;
import com.hotketok.domain.enums.ChatRoomType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatRoomResponse(
        Long roomId,
        String lastMessageContent,
        LocalDateTime lastMessageTime,
        long unreadCount,
        List<ParticipantResponse> participants,
        String address,
        String estimateStatus
) {

    public ChatRoomResponse(ChatRoom chatRoom, ChatMessage lastMessage, long unreadCount,
                            Map<Long, UserProfileResponse> userProfiles,
                            Map<Long, RequestFormAddressStatusResponse> requestFormMap) {
        this(
                chatRoom.getId(),
                lastMessage != null ? lastMessage.getContent() : "아직 메시지가 없습니다.",
                lastMessage != null ? lastMessage.getCreatedAt() : chatRoom.getCreatedAt(),
                unreadCount,
                chatRoom.getParticipants().stream()
                        .map(participant -> {
                            UserProfileResponse userProfile = userProfiles.get(participant.getUserId());
                            return new ParticipantResponse(participant, userProfile);
                        })
                        .collect(Collectors.toList()),

                // 공사업체 포함됐다면 address, status 반환 포함
                (chatRoom.getRoomType() == ChatRoomType.VENDOR_ESTIMATE && requestFormMap != null)
                        ? requestFormMap.get(chatRoom.getRequestFormId()).address()
                        : null,

                (chatRoom.getRoomType() == ChatRoomType.VENDOR_ESTIMATE && requestFormMap != null)
                        ? requestFormMap.get(chatRoom.getRequestFormId()).status().name()
                        : null
        );
    }
}