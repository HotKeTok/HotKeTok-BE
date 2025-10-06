package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.RequestFormAuthorResponse;
import com.hotketok.dto.internalApi.RequestFormDataResponse;
import com.hotketok.dto.internalApi.RequestFormListResponse;
import com.hotketok.dto.internalApi.UpdateStatusRequest;
import com.hotketok.service.RequestFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/requestform-service")
@RequiredArgsConstructor
public class RequestFormInternalController {

    private final RequestFormService requestFormService;

    @GetMapping("/{requestFormId}")
    public RequestFormDataResponse getRequestFormData(@PathVariable Long requestFormId) {
        return requestFormService.getRequestFormDataById(requestFormId);
    }

    // 요청서 작성자 확인
    @GetMapping("/{requestFormId}/author")
    public RequestFormAuthorResponse getRequestFormAuthor(@PathVariable Long requestFormId) {
        return requestFormService.getRequestFormAuthorById(requestFormId);
    }

    // 요청서 상태 변경
    @PutMapping("/{requestFormId}/status")
    public void updateRequestFormStatus(
            @PathVariable Long requestFormId,
            @RequestBody UpdateStatusRequest request
    ) {
        requestFormService.updateStatus(requestFormId, request.status());
    }

    // id로 요청서 목록 조회
    @PostMapping("/info-list")
    public List<RequestFormListResponse> getRequestFormsByIds(@RequestBody List<Long> requestFormIds) {
        return requestFormService.getRequestFormsByIds(requestFormIds);
    }
}
