package com.hotketok.internalApi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "chatting-service", url = "${client.chatting-service.url}")
public interface ChatServiceClient {
    @GetMapping("/internal/chatting-service/rooms/by-requestform/{requestFormId}")
    Long getRoomIdByRequestFormId(@PathVariable("requestFormId") Long requestFormId);
}

