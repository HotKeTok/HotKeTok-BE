package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.HouseInfoResponse;
import com.hotketok.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.hotketok.dto.internalApi.GetHouseInfoByAddressResponse;

@RestController
@RequestMapping("/internal/house-service")
@RequiredArgsConstructor
public class HouseInternalController {

    private final HouseService houseService;

    @GetMapping("/houses")
    public List<HouseInfoResponse> getMatchedHousesByTenantAndAddress(
            @RequestParam Long tenantId,
            @RequestParam String address) {
        return houseService.getMatchedHousesByTenantAndAddress(tenantId, address);
    }
    @GetMapping("/residents")
    public List<HouseInfoResponse> getResidentsByUserId(@RequestParam String address) {
        return houseService.findResidentsByAddress(address);
    }

    @GetMapping("/user/{userId}")
    public HouseInfoResponse getHouseInfoByUserId(@PathVariable("userId") Long userId) {
        return houseService.findHouseInfoByUserId(userId);
    }

    @GetMapping("/find-house-by-address")
    public GetHouseInfoByAddressResponse getHouseInfoByAddress(@RequestParam("userId") Long userId,
                                                               @RequestParam("role") String role,
                                                               @RequestParam("address") String address,
                                                               @RequestParam("number") String number) {
        return houseService.getHouseInfoByAddress(userId,role,address,number);
    }
}
