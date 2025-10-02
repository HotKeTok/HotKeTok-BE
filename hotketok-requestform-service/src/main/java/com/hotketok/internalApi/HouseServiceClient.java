package com.hotketok.internalApi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "house-service", url = "${client.house-service.url}")
public interface HouseServiceClient {

    @GetMapping("/get-ownerId/{userId}")
    Long getOwnerId(@PathVariable("userId") Long userId, @RequestParam("address") String address, @RequestParam("number") String number);
}
