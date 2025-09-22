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

    @MessageMapping("/chat/message")
    public void message(MessageRequest message) {
        log.info("Received Message (Sync): {}", message.getContent());

        ChatMessage savedMessage = chatService.saveMessage(message.getRoomId(), message.getSenderId(), message.getContent());

        ChatMessageResponse messageResponse = new ChatMessageResponse(savedMessage);

        messagingTemplate.convertAndSend("/sub/chat/room/" + messageResponse.getRoomId(), messageResponse);
    }
}
