package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.EstimateInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "estimate-service", url = "${client.estimate-service.url}")
public interface EstimateServiceClient {
    @GetMapping("/internal/estimates")
    List<EstimateInfoResponse> getEstimatesByRequestFormId(@RequestParam("requestFormId") Long requestFormId);
}
