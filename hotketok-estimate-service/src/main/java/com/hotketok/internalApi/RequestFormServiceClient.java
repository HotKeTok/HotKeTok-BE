package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.RequestFormAuthorResponse;
import com.hotketok.dto.internalApi.RequestFormResponse;
import com.hotketok.dto.internalApi.UpdateStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "requestform-service", url = "${client.requestform-service.url}")
public interface RequestFormServiceClient {
    @GetMapping("/internal/requestform-service/{requestFormId}")
    RequestFormResponse getRequestFormData(@PathVariable("requestFormId") Long requestFormId);

    // 요청서의 작성자 확인
    @GetMapping("/internal/requestform-service/{requestFormId}/author")
    RequestFormAuthorResponse getRequestFormAuthor(@PathVariable("requestFormId") Long requestFormId);

    @PutMapping("/internal/requestform-service/{requestFormId}/status")
    void updateRequestFormStatus(@PathVariable("requestFormId") Long requestFormId, @RequestBody UpdateStatusRequest request);

    @GetMapping("/internal/requestform-service/by-author")
    List<Long> getRequestFormIdsByAuthorId(@RequestParam("authorId") Long authorId);
}
