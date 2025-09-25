package com.hotketok.externalApi;

import com.hotketok.domain.House;
import com.hotketok.dto.*;
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

    // 입주민 요청 (주소/동/호수 기반)
    @PostMapping("/tenant-request")
    public RegisterTenantResponse registerTenant(/*@RequestHeader("userId")*/ @RequestParam Long tenantId,
                                                 @RequestBody RegisterTenantRequest request) {
        return houseService.registerTenant(tenantId,request.address(),request.floor(),request.number());
    }

    // 집주인 요청 목록
    @GetMapping("/tenant-requestList")
    public List<TenantRequestResponse> getTenantRequestList(/*@RequestHeader("userId")*/ @RequestParam Long ownerId) {
        return houseService.getTenantRequestList(ownerId);
    }

    // 집주인 승인
    @PostMapping("/tenant-approve/{houseId}")
    public ResponseEntity<Void> tenantApprove(@PathVariable Long houseId,
            /*@RequestHeader("userId")*/ @RequestParam Long ownerId) {
        houseService.approveTenant(houseId, ownerId);
        return ResponseEntity.ok().build();
    }

    // 집주인 거절
    @PostMapping("/tenant-reject/{houseId}")
    public ResponseEntity<Void> tenantReject(@PathVariable Long houseId,
            /*@RequestHeader("userId")*/ @RequestParam Long ownerId) {
        houseService.rejectTenant(houseId, ownerId);
        return ResponseEntity.ok().build();
    }

}

