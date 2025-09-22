package com.hotketok.dto.internalApi;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CreateChatRoomRequest {
    private String roomName;
    private List<Long> participantUserIds; // 참여자 여러 명의 List
}
