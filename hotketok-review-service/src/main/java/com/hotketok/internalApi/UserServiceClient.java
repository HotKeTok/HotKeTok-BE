package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.CurrentAddressResponse;
import com.hotketok.dto.internalApi.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserServiceClient {

    @PostMapping("/internal/user-service/profiles")
    List<UserProfileResponse> getUserProfilesByIds(@RequestBody List<Long> userIds);

    @GetMapping("/internal/user-service/{userId}/current-address")
    CurrentAddressResponse getCurrentAddress(@PathVariable("userId") Long userId);

    @GetMapping("/internal/user-service/residents-by-address")
    List<Long> getUserIdsByAddress(@RequestParam("address") String address);
}