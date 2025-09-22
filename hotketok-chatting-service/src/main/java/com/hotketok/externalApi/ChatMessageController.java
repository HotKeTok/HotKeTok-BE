package com.hotketok.externalApi;

import com.hotketok.dto.internalApi.ChatMessageResponse;
import com.hotketok.dto.internalApi.ChatRoomResponse;
import com.hotketok.dto.internalApi.CreateChatRoomRequest;
import com.hotketok.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping("/rooms")
    public Long createChatRoom(@RequestBody CreateChatRoomRequest request) {
        return chatService.createChatRoom(request);
    }

    // 특정 유저의 채팅방 목록 조회
    @GetMapping("/users/{userId}/rooms")
    public List<ChatRoomResponse> findChatRoomsByUserId(@PathVariable Long userId) {
        return chatService.findChatRoomsByUserId(userId);
    }

    // 특정 채팅방의 메시지 목록 조회
    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageResponse> findMessagesByRoomId(@PathVariable Long roomId) {
        return chatService.findMessagesByRoomId(roomId);
    }
}