package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.HouseInfoResponse;
import com.hotketok.dto.internalApi.HouseUnitResponse;
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

    @GetMapping("/get-ownerId/{userId}")
    public Long getOwnerId(@PathVariable("userId") Long userId,
                          @RequestParam("address") String address,
                          @RequestParam("number") String number) {
        return houseService.getOwnerId(userId,address,number);
    }

    @PostMapping("/units")
    public List<HouseUnitResponse> getUnitNumbersByUserIds(@RequestBody List<Long> userIds) {
        return houseService.findUnitNumbersByUserIds(userIds);
    }

    // 마이페이지 사용자 정보 조회 -> 주택 태그들을 가져오기 위한 내부 API
    @GetMapping("/get/houseTag")
    public List<String> getHouseTag(@RequestParam("address") String address,
                                    @RequestParam("number") String number){
        return houseService.getHouseTag(address,number);
    }
}
