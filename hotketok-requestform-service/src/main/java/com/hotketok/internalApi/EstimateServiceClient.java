package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.EstimateInfoResponse;
import com.hotketok.dto.internalApi.EstimatePriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "estimate-service", url = "${client.estimate-service.url}")
public interface EstimateServiceClient {
    @GetMapping("/internal/estimate-service")
    List<EstimateInfoResponse> getEstimatesByRequestFormId(@RequestParam("requestFormId") Long requestFormId);

    @GetMapping("/internal/estimate-service/estimate-price/{requestFormId}")
    EstimatePriceResponse getEstimatePriceByRequestFormId(@PathVariable Long requestFormId);
}
