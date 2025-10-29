package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.CreateChatRoomRequest;
import com.hotketok.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/chatting-service")
@RequiredArgsConstructor
@Slf4j
public class ChattingInternalController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    public Long createChatRoom(@RequestBody CreateChatRoomRequest request) {
        log.info("Internal API call received: createChatRoom with participants: {}", request.participantUserIds());
        return chatService.createChatRoom(request);
    }
}
