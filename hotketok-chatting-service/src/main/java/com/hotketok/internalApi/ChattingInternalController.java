package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.CreateChatRoomRequest;
import com.hotketok.dto.internalApi.MessageRequest;
import com.hotketok.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/messages")
    public void sendMessage(@RequestBody MessageRequest request) {
        log.info("Internal API call received: saveMessage for room {} by sender {}", request.roomId(), request.senderId());
        chatService.saveMessage(request.senderId(), request);
    }

    @GetMapping("/rooms/by-requestform/{requestFormId}")
    public Long getRoomIdByRequestFormId(@PathVariable("requestFormId") Long requestFormId) {
        log.info("Internal API call received: getRoomIdByRequestFormId for requestFormId: {}", requestFormId);

        Long roomId = chatService.findRoomIdByRequestFormId(requestFormId);
        if (roomId == null) {
            return null;
        }
        return roomId;
    }
}
