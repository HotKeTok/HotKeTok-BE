package com.hotketok.internalApi;

import com.hotketok.dto.SignUpRequest;
import com.hotketok.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserServiceClient {
    @PostMapping("/internal/users/save")
    void save(@RequestBody SignUpRequest req);

    @GetMapping("/internal/users/find-by-logInId")
    UserInfo findByLogInId(@RequestParam String logInId);

    @GetMapping("/internal/users/find-by-id/{id}")
    UserInfo findById(@PathVariable Long id);
}
