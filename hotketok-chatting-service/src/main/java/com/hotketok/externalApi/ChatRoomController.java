package com.hotketok.externalApi;

import com.hotketok.dto.internalApi.ChatMessageResponse;
import com.hotketok.dto.internalApi.ChatRoomResponse;
import com.hotketok.dto.internalApi.CreateChatRoomRequest;
// import com.hotketok.security.UserPrincipal; // 토큰 적용 후 도입
import com.hotketok.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatService chatService;

    // 채팅방 생성
    @PostMapping("/rooms")
    public Long createChatRoom(@RequestBody CreateChatRoomRequest request) {
        return chatService.createChatRoom(request);
    }

    // 특정 유저의 채팅방 목록 조회
    @GetMapping("/users/rooms")
    public List<ChatRoomResponse> findChatRoomsByUserId() {
        Long userId = 101L; // 임시 데이터
        return chatService.findChatRoomsByUserId(userId);
    }

    // 채팅방 삭제
    @DeleteMapping("/rooms")
    public ResponseEntity<Void> deleteChatRoom(@RequestParam Long roomId) {
        Long userId = 101L; // 임시 데이터
        chatService.deleteChatRoom(userId, roomId);
        return ResponseEntity.noContent().build(); // 성공적으로 삭제되었으면 204 No Content
    }

    // 특정 채팅방의 채팅 내용 조회
    @GetMapping("/rooms/messages")
    public List<ChatMessageResponse> findMessagesByRoomId(@RequestParam Long roomId) {
        Long userId = 101L; // 임시 데이터
        return chatService.findMessagesByRoomId(userId, roomId);
    }
}