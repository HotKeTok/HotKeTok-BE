package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/internal/users/roles")
    List<UserProfileResponse> getUserRolesByIds(@RequestBody List<Long> userIds);
}
