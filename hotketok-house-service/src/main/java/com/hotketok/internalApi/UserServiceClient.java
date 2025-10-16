package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserServiceClient {

    @PostMapping("/internal/user-service/change-role/{userId}")
    void updateRole(@PathVariable("userId") Long userId, @RequestParam("role") Role role);

    @GetMapping("/internal/user-service/get-tenantInfo/{userId}")
    TenantInfoResponse getTenantInfo(@PathVariable("userId") Long userId);

    @GetMapping("/internal/user-service/{userId}/current-address")
    CurrentAddressResponse getCurrentAddress(@PathVariable("userId") Long userId);

    @GetMapping ("/internal/user-service/current-address-and-number/{userId}")
    CurrentAddressAndNumberResponse getCurrentAddressAndNumber(@PathVariable("userId") Long userId);

    @PostMapping(value = "/internal/user-service/change/current-address-and-number/{userId}")
    void changeCurrentAddressAndNumber(@PathVariable("userId") Long userId, @RequestParam("address") String address, @RequestParam("number") String number);
}

