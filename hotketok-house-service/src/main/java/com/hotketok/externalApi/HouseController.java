package com.hotketok.externalApi;

import com.hotketok.dto.RegisterHouseRequest;
import com.hotketok.dto.RegisterHouseResponse;
import com.hotketok.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/houses-service")
@RequiredArgsConstructor
public class HouseController {
    private final HouseService houseService;

    // 집주인 집 등록
    @PostMapping("/register")
    public RegisterHouseResponse registerHouse(/*@RequestHeader("userId")*/ @RequestParam Long ownerId,
                                                                            @RequestBody List<RegisterHouseRequest> requests) {
        return houseService.registerHouse(ownerId, requests);
    }

    // 관리자 승인
    @PostMapping("/admin-approve/{houseId}")
    public ResponseEntity<Void> approveHouse(@PathVariable Long houseId) {
        houseService.approveHouse(houseId);
        return ResponseEntity.ok().build();
    }

    // 관리자 거절
    @DeleteMapping("/admin-reject/{houseId}")
    public ResponseEntity<Void> adminReject(@PathVariable Long houseId) {
        houseService.rejectHouse(houseId);
        return ResponseEntity.ok().build();
    }

}

