package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.VendorProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "vendor-service", url = "${client.vendor-service.url}")
public interface VendorServiceClient {
    @PostMapping("/internal/vendor-service/profiles")
    List<VendorProfileResponse> getVendorProfilesByIds(@RequestBody List<Long> vendorIds);
}
