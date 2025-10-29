package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.CurrentAddressAndNumberResponse;
import com.hotketok.dto.internalApi.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserServiceClient {
    @PostMapping("/internal/user-service/profiles") // 채팅방 목록 조회에서 프로필 정보 조회에 사용
    List<UserProfileResponse> getUserProfilesByIds(@RequestBody List<Long> userIds);

    @GetMapping("/internal/user-service/current-address-and-number/{userId}")
    CurrentAddressAndNumberResponse getCurrentAddressAndNumber(@PathVariable("userId") Long userId);
}
