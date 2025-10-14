package com.hotketok.internalApi;

import com.hotketok.domain.enums.Status;
import com.hotketok.dto.RequestFormDateResponse;
import com.hotketok.dto.internalApi.RequestFormDetailResponse;
import com.hotketok.dto.internalApi.RequestFormSimpleResponse;
import com.hotketok.dto.internalApi.ScheduledRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "requestform-service", url = "${client.requestform-service.url}")
public interface RequestFormServiceClient {
    @PostMapping("/internal/requestform-service/info-list")
    List<RequestFormDetailResponse> getRequestFormsByIds(@RequestBody List<Long> requestFormIds);

    @GetMapping("/internal/requestform-service/{requestFormId}/detail")
    RequestFormDetailResponse getRequestFormDetail(@PathVariable("requestFormId") Long requestFormId);

    // 받은 수리 요청 조회
    @GetMapping("/internal/requestform-service/by-status")
    List<RequestFormSimpleResponse> getRequestFormsByStatus(@RequestParam("statuses") List<Status> statuses);

    @PostMapping("/internal/requestform-service/scheduled-info")
    List<RequestFormDateResponse> getScheduledRequestForms(@RequestBody ScheduledRequest request);
}
