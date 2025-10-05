package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.VendorInfoResponse;
import com.hotketok.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}