package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.CurrentAddressResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/internal/user-service/{userId}/current-address")
    CurrentAddressResponse getCurrentAddress(@PathVariable("userId") Long userId);
}
