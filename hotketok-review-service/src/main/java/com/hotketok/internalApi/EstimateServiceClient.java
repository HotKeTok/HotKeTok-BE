package com.hotketok.internalApi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "estimate-service", url = "${client.estimate-service.url}")
public interface EstimateServiceClient {
    @GetMapping("/internal/estimate-service/check-completion")
    boolean hasCompletedWork(@RequestParam("userId") Long userId, @RequestParam("vendorId") Long vendorId);
}
