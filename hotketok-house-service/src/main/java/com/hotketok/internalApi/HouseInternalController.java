package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.HouseInfoResponse;
import com.hotketok.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/houses")
@RequiredArgsConstructor
public class HouseInternalController {

    private final HouseService houseService;

    @GetMapping("/user/{userId}/id")
    public ResponseEntity<HouseInfoResponse> getHouseInfoByUserId(@PathVariable("userId") Long userId) {
        HouseInfoResponse response = houseService.findHouseInfoByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{houseId}/residents")
    public ResponseEntity<List<HouseInfoResponse>> getResidentsByHouseId(@PathVariable("houseId") Long houseId) {
        List<HouseInfoResponse> response = houseService.findResidentsByHouseId(houseId);
        return ResponseEntity.ok(response);
    }
}