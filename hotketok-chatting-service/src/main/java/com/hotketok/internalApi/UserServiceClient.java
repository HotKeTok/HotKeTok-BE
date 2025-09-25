package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @PostMapping("/internal/users/profiles") // 채팅방 목록 조회에서 프로필 정보 조회에 사용
    List<UserProfileResponse> getUserProfilesByIds(@RequestBody List<Long> userIds);
}
