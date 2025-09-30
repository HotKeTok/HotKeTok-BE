package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.HouseIdResponse;
import com.hotketok.dto.internalApi.HouseInfoResponse;
import com.hotketok.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/user/{userId}")
    public HouseInfoResponse getHouseInfoByUserId(@PathVariable("userId") Long userId) {
        return houseService.findHouseInfoByUserId(userId);
    }

    @GetMapping("/user/{userId}/id")
    public HouseIdResponse getHouseIdByUserId(@PathVariable("userId") Long userId) {
        return houseService.findHouseIdByUserId(userId);
    }

    @GetMapping("/{houseId}/residents")
    public List<HouseInfoResponse> getResidentsByHouseId(@PathVariable("houseId") Long houseId) {
        return houseService.findResidentsByHouseId(houseId);
    }

    @GetMapping("/find-house-by-address")
    public GetHouseInfoByAddressResponse getHouseInfoByAddress(@RequestParam("userId") Long userId,
                                                               @RequestParam("role") String role, @RequestParam("address") String address) {
        return houseService.getHouseInfoByAddress(userId,role,address);
    }
}
