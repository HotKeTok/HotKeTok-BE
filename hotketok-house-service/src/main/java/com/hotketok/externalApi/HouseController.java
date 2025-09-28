package com.hotketok.externalApi;

import com.hotketok.domain.House;
import com.hotketok.dto.*;
import com.hotketok.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

@RestController
@RequestMapping("/api/houses-service")
@RequiredArgsConstructor
public class HouseController {
    private final HouseService houseService;

    // 집주인 집 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RegisterHouseResponse registerHouse(@RequestHeader("userId") Long ownerId,
                                               @RequestPart("file") MultipartFile file,
                                               @RequestPart("data") RegisterHouseRequest request) {
        return houseService.registerHouse(ownerId, file, request);
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
    public RegisterTenantResponse registerTenant(@RequestHeader("userId") Long tenantId,
                                                 @RequestBody RegisterTenantRequest request) {
        return houseService.registerTenant(tenantId,request);
    }

    // 집주인 요청 목록
    @GetMapping("/tenant-requestList")
    public List<TenantRequestResponse> getTenantRequestList(@RequestHeader("userId") Long ownerId) {
        return houseService.getTenantRequestList(ownerId);
    }

    // 집주인 승인
    @PostMapping("/tenant-approve/{houseId}")
    public ResponseEntity<Void> tenantApprove(@PathVariable Long houseId,
            @RequestHeader("userId") Long ownerId) {
        houseService.approveTenant(houseId, ownerId);
        return ResponseEntity.ok().build();
    }

    // 집주인 거절
    @PostMapping("/tenant-reject/{houseId}")
    public ResponseEntity<Void> tenantReject(@PathVariable Long houseId,
            @RequestHeader("userId") Long ownerId) {
        houseService.rejectTenant(houseId, ownerId);
        return ResponseEntity.ok().build();
    }

}

