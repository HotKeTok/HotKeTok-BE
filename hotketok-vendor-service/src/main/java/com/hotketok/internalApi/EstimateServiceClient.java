package com.hotketok.internalApi;

import com.hotketok.domain.enums.Status;
import com.hotketok.dto.internalApi.EstimateDateResponse;
import com.hotketok.dto.internalApi.EstimateInfoResponse;
import com.hotketok.dto.internalApi.EstimateStatusCountResponse;
import com.hotketok.dto.internalApi.SimpleEstimateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "estimate-service", url = "${client.estimate-service.url}")
public interface EstimateServiceClient {
    @GetMapping("/internal/estimate-service")
    List<EstimateInfoResponse> getEstimatesByVendorId(@RequestParam("vendorId") Long vendorId);

    @GetMapping("/internal/estimate-service/matching")
    List<EstimateInfoResponse> getMatchingEstimatesByVendorId(@RequestParam("vendorId") Long vendorId);

    @GetMapping("/internal/estimate-service/completed")
    List<EstimateInfoResponse> getCompletedEstimatesByVendorId(@RequestParam("vendorId") Long vendorId);

    @GetMapping("/internal/estimate-service/{estimateId}")
    SimpleEstimateResponse getEstimateById(@PathVariable("estimateId") Long estimateId);

    @GetMapping("/internal/estimate-service/counts")
    EstimateStatusCountResponse getEstimateCounts(@RequestParam("vendorId") Long vendorId);

    @GetMapping("/internal/estimate-service/by-status")
    List<EstimateDateResponse> getEstimatesByStatus(@RequestParam("vendorId") Long vendorId, @RequestParam("status") Status status);
}