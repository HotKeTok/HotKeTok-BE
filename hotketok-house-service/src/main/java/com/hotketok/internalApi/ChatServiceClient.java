package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.CreateChatRoomRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "chatting-service",  url = "${client.chatting-service.url}")
public interface ChatServiceClient {
    @PostMapping("/internal/chatting-service/rooms")
    CreateChatRoomRequest createChatRoom(@RequestBody CreateChatRoomRequest request);
}
