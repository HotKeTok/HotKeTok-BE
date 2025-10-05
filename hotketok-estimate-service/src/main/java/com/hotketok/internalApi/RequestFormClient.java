package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.RequestFormResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "requestform-service", url = "${client.requestform-service.url}")
public interface RequestFormClient {
    @GetMapping("/internal/requestform-service/{requestFormId}")
    RequestFormResponse getRequestFormData(@PathVariable("requestFormId") Long requestFormId);
}
