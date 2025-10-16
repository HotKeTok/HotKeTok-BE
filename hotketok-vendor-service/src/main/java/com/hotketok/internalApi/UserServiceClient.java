package com.hotketok.internalApi;

import com.hotketok.domain.enums.Role;
import com.hotketok.dto.internalApi.UserInfoDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserServiceClient {

    @PostMapping("/internal/user-service/change-role/{userId}")
    void updateRole(@PathVariable("userId") Long userId, @RequestParam("role") Role role);

    @PostMapping("/internal/user-service/change/onboarding-stage-flag/{userId}")
    void updateOnboardingStageFlag(@PathVariable("userId") Long userId, @RequestParam("flag") boolean flag);

    @PostMapping("/internal/user-service/profiles-detail")
    List<UserInfoDetailResponse> getUserInfosByIds(@RequestBody List<Long> userIds);

    @GetMapping("/internal/user-service/profiles-detail/{userId}")
    UserInfoDetailResponse getUserInfoById(@PathVariable("userId") Long userId);
}