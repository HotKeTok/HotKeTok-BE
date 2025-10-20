package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.HouseUnitResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "house-service")
public interface HouseServiceClient {
    @PostMapping("/internal/house-service/units")
    List<HouseUnitResponse> getUnitNumbersByUserIds(@RequestBody List<Long> userIds);
}