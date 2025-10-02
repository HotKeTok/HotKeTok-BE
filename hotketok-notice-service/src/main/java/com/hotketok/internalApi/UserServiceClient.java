package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.CurrentAddressResponse;
import com.hotketok.dto.internalApi.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/internal/user-service/{userId}/current-address")
    CurrentAddressResponse getCurrentAddress(@PathVariable("userId") Long userId);

    @GetMapping("/internal/user-service/profiles/{userId}")
    UserProfileResponse getUserProfileById(@PathVariable("userId") Long userId);
}

