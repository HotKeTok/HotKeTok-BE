package com.hotketok.externalApi;

import com.hotketok.domain.ChatMessage;
import com.hotketok.dto.internalApi.ChatMessageResponse;
import com.hotketok.dto.internalApi.MessageRequest;
import com.hotketok.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 웹소켓으로 들어오는 메시지 처리
    // /pub/chat/message 전송 시 이 메소드 호출
    @MessageMapping("/chat/message")
    public void message(MessageRequest message) {
        log.info("Received WebSocket Message: {}", message.content());

        ChatMessage savedMessage = chatService.saveMessage(message);
        ChatMessageResponse messageResponse = new ChatMessageResponse(savedMessage);

        // 채팅방 속해있는 모든 유저에게 전달
        messagingTemplate.convertAndSend("/sub/chat/room/" + messageResponse.roomId(), messageResponse);
    }
}

