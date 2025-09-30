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
    public ResponseEntity<HouseInfoResponse> getHouseInfoByUserId(@PathVariable("userId") Long userId) {
        HouseInfoResponse response = houseService.findHouseInfoByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/id")
    public ResponseEntity<HouseIdResponse> getHouseIdByUserId(@PathVariable("userId") Long userId) {
        HouseIdResponse response = houseService.findHouseIdByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{houseId}/residents")
    public ResponseEntity<List<HouseInfoResponse>> getResidentsByHouseId(@PathVariable("houseId") Long houseId) {
        List<HouseInfoResponse> response = houseService.findResidentsByHouseId(houseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-house-by-address")
    public GetHouseInfoByAddressResponse getHouseInfoByAddress(@RequestParam("userId") Long userId,
                                                               @RequestParam("role") String role, @RequestParam("address") String address) {
        return houseService.getHouseInfoByAddress(userId,role,address);
    }
}
