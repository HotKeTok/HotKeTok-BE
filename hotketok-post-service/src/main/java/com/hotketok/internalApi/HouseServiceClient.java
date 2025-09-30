package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.HouseIdResponse;
import com.hotketok.dto.internalApi.HouseInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "house-service", url = "${client.house-service.url}")
public interface HouseServiceClient {

    @GetMapping("/internal/houses/user/{userId}")
    HouseInfoResponse getHouseInfoByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/internal/houses/user/{userId}/id")
    HouseIdResponse getHouseIdByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/internal/houses/{houseId}/residents")
    List<HouseInfoResponse> getResidentsByHouseId(@PathVariable("houseId") Long houseId);
}

