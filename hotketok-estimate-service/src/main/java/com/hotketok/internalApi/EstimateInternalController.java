package com.hotketok.internalApi;

import com.hotketok.domain.enums.Status;
import com.hotketok.dto.internalApi.EstimateInfoResponse;
import com.hotketok.dto.internalApi.SimpleEstimateResponse;
import com.hotketok.service.EstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/internal/estimate-service")
@RequiredArgsConstructor
public class EstimateInternalController {
    private final EstimateService estimateService;

    @GetMapping
    public List<EstimateInfoResponse> getEstimatesByVendorId(@RequestParam Long vendorId) {
        return estimateService.findEstimatesByVendorId(vendorId);
    }

    @GetMapping("/matching")
    public List<SimpleEstimateResponse> getMatchingEstimates(@RequestParam Long vendorId) {
        return estimateService.findEstimatesByVendorIdAndStatus(vendorId, Status.MATCHING);
    }

    @GetMapping("/completed")
    public List<SimpleEstimateResponse> getCompletedEstimates(@RequestParam Long vendorId) {
        return estimateService.findEstimatesByVendorIdAndStatus(vendorId, Status.COMPLETED);
    }
}