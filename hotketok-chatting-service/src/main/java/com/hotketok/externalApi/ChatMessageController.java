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

import java.security.Principal;

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
        Long senderId;
        try {
            senderId = message.senderId();

            if (senderId == null) {
                log.error("SenderId is null in the request body. Room: {}", message.roomId());
                return;
            }

        } catch (NullPointerException e) {
            log.error("MessageRequest or senderId is null", e);
            // 인증 정보 없으면 중단
            return;
        }

        log.info("Received WebSocket Message from {} (Room {}): {}", senderId, message.roomId(), message.content());
        ChatMessage savedMessage = chatService.saveMessage(senderId, message);
        ChatMessageResponse messageResponse = new ChatMessageResponse(savedMessage);

        messagingTemplate.convertAndSend("/topic/chat/room/" + messageResponse.roomId(), messageResponse);
    }
}

