package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.EstimateInfoResponse;
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
}