package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.HouseInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "house-service", url = "${client.house-service.url}")
public interface HouseServiceClient {

    @GetMapping("/internal/houses/user/{userId}")
    HouseInfoResponse getHouseInfoByUserId(@PathVariable("userId") Long userId);
}

