package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.VendorCategoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "vendor-service")
public interface VendorServiceClient {
    @PostMapping("/internal/vendor-service/categories")
    List<VendorCategoryResponse> getVendorCategoriesByIds(@RequestBody List<Long> vendorIds);
}
