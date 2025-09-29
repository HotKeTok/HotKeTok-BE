package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.Role;
import com.hotketok.dto.internalApi.TenantInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserServiceClient {

    @PostMapping("/internal/users/change-role/{userId}")
    void updateRole(@PathVariable("userId") Long userId, @RequestParam("role") Role role);

    @GetMapping("/internal/users/get-tenantInfo/{userId}")
    TenantInfoResponse getTenantInfo(@PathVariable("userId") Long userId);
}

