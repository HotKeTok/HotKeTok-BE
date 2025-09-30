package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.HouseInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "house-service", url = "${client.house-service.url}")
public interface HouseServiceClient {

    @GetMapping("/internal/house-service/user/{userId}")
    HouseInfoResponse getHouseInfoByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/internal/house-service/residents")
    List<HouseInfoResponse> getResidentsByAddress(@RequestParam String address);

    @GetMapping("/internal/house-service/houses")
    List<HouseInfoResponse> getMatchedHousesByTenantAndAddress(
            @RequestParam("tenantId") Long tenantId,
            @RequestParam("address") String address
    );
}

