package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.UserRoleInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/internal/users/roles")
    List<UserRoleInfoResponse> getUserRolesByIds(@RequestBody List<Long> userIds);
}
