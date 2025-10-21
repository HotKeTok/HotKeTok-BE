package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.ReviewStatsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "review-service", url = "${client.review-service.url}")
public interface ReviewServiceClient {
    @GetMapping("/internal/review-service/count")
    int getReviewCountByVendorId(@RequestParam("vendorId") Long vendorId);

    @GetMapping("/internal/review-service/stats")
    ReviewStatsResponse getReviewStatsByVendorId(@RequestParam("vendorId") Long vendorId);
}