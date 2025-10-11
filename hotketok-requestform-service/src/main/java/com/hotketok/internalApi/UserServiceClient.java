package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.CurrentAddressAndNumberResponse;
import com.hotketok.dto.internalApi.UserInfoDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserServiceClient {

    @GetMapping("/internal/user-service/current-address-and-number/{userId}")
    CurrentAddressAndNumberResponse getCurrentAddressAndNumber(@PathVariable("userId") Long userId);

    @PostMapping("/internal/user-service/profiles-detail")
    List<UserInfoDetailResponse> getUserInfosByIds(@RequestBody List<Long> userIds);
}
