package com.hotketok.externalApi;

import com.hotketok.dto.internalApi.ChatMessageResponse;
import com.hotketok.dto.internalApi.ChatRoomResponse;
import com.hotketok.dto.internalApi.CreateChatRoomRequest;
import com.hotketok.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    public Long createChatRoom(@RequestBody CreateChatRoomRequest request) {
        return chatService.createChatRoom(request);
    }

    @GetMapping("/rooms")
    public List<ChatRoomResponse> getChatRooms(@RequestParam Long userId) {
        return chatService.findChatRoomsByUserId(userId);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public List<ChatMessageResponse> getChatMessages(@PathVariable Long roomId) {
        return chatService.findMessagesByRoomId(roomId);
    }
}