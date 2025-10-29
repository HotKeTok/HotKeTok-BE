package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.CreateChatRoomRequest;
import com.hotketok.dto.internalApi.CreateChatRoomResponse;
import com.hotketok.dto.internalApi.MessageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "chatting-service", url = "${client.chatting-service.url}")
public interface ChatServiceClient {
    @GetMapping("/internal/chatting-service/rooms/by-requestform/{requestFormId}")
    Long getRoomIdByRequestFormId(@PathVariable("requestFormId") Long requestFormId);

    @PostMapping("/internal/chatting-service/messages")
    void sendMessage(@RequestBody MessageRequest request);

    @PostMapping("/internal/chatting-service/rooms")
    CreateChatRoomResponse createChatRoom(@RequestBody CreateChatRoomRequest request);
}

