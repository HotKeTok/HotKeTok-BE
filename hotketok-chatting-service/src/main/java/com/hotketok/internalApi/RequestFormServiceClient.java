package com.hotketok.internalApi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "requestform-service", url = "${client.requestform-service.url}")
public interface RequestFormServiceClient {
    @PostMapping("/internal/requestform-service/info-list")
    List<RequestFormDataResponse> getRequestFormsByIds(@RequestBody List<Long> requestFormIds);
}