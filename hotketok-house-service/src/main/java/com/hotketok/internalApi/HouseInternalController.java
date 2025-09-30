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

    @GetMapping("/user/{userId}")
    public HouseInfoResponse getHouseInfoByUserId(@PathVariable("userId") Long userId) {
        return houseService.findHouseInfoByUserId(userId);
    }

    @GetMapping("/residents")
    public List<HouseInfoResponse> getResidentsByUserId(@RequestParam String address) {
        return houseService.findResidentsByAddress(address);
    }

    @GetMapping("/find-house-by-address")
    public GetHouseInfoByAddressResponse getHouseInfoByAddress(@RequestParam("userId") Long userId,
                                                               @RequestParam("role") String role, @RequestParam("address") String address) {
        return houseService.getHouseInfoByAddress(userId,role,address);
    }
}
