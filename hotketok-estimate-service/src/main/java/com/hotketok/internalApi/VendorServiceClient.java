package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.VendorInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

@FeignClient(name = "vendor-service", url = "${client.vendor-service.url}")
public interface VendorServiceClient {

    @PostMapping("/internal/vendors/info")
    List<VendorInfoResponse> getVendorInfosByIds(@RequestBody List<Long> vendorIds);
}