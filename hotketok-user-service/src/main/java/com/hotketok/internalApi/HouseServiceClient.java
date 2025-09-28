package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.MyPageHouseInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "house-service", url = "${client.house-service.url}")
public interface HouseServiceClient {
    @GetMapping("/internal/house-service/mypage/house")
    MyPageHouseInfoResponse getMyPageHouseInfo( @RequestParam("userId") Long userId,
                                                @RequestParam("role") String role);
}
