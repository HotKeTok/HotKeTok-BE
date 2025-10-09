package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.RequestFormDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "requestform-service", url = "${client.requestform-service.url}")
public interface RequestFormServiceClient {
    @PostMapping("/internal/requestform-service/info-list")
    List<RequestFormDetailResponse> getRequestFormsByIds(@RequestBody List<Long> requestFormIds);

    @GetMapping("/internal/requestform-service/{requestFormId}/detail")
    RequestFormDetailResponse getRequestFormDetail(@PathVariable("requestFormId") Long requestFormId);
}
