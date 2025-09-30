package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.GetHouseInfoByAddressResponse;
import com.hotketok.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/house-service")
@RequiredArgsConstructor
public class HouseInternalController {
    private final HouseService houseService;

    @GetMapping("/find-house-by-address")
    public GetHouseInfoByAddressResponse getHouseInfoByAddress(@RequestParam("userId") Long userId,
                                                               @RequestParam("role") String role, @RequestParam("address") String address) {
        return houseService.getHouseInfoByAddress(userId,role,address);
    }
}
