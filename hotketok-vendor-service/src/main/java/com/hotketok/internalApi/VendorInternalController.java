package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.VendorInfoResponse;
import com.hotketok.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/vendors")
@RequiredArgsConstructor
public class VendorInternalController {

    private final VendorService vendorService;

    @PostMapping("/info")
    public List<VendorInfoResponse> getVendorInfosByIds(@RequestBody List<Long> vendorIds) {
        return vendorService.findVendorInfosByIds(vendorIds);
    }

    // 단일 공사업체 정보 조회
    @GetMapping("/{vendorId}")
    public VendorInfoResponse getVendorInfoById(@PathVariable Long vendorId) {
        return vendorService.findVendorInfoById(vendorId);
    }
}