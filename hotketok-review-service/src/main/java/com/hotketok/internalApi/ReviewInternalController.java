package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.ReviewStatsResponse;
import com.hotketok.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/review-service")
@RequiredArgsConstructor
public class ReviewInternalController {

    private final ReviewService reviewService;

    @GetMapping("/count")
    public int getReviewCountByVendorId(@RequestParam("vendorId") Long vendorId) {
        return reviewService.getReviewCountByVendorId(vendorId);
    }

    @GetMapping("/stats")
    public ReviewStatsResponse getReviewStatsByVendorId(@RequestParam("vendorId") Long vendorId) {
        return reviewService.getReviewStatsByVendorId(vendorId);
    }
}