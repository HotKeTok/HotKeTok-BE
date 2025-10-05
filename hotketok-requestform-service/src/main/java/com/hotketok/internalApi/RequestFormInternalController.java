package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.RequestFormDataResponse;
import com.hotketok.dto.internalApi.UpdateStatusRequest;
import com.hotketok.service.RequestFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/requestform-service")
@RequiredArgsConstructor
public class RequestFormInternalController {

    private final RequestFormService requestFormService;

    @GetMapping("/{requestFormId}")
    public RequestFormDataResponse getRequestFormData(@PathVariable Long requestFormId) {
        return requestFormService.getRequestFormDataById(requestFormId);
    }

    // 요청서 상태 변경
    @PatchMapping("/{requestFormId}/status")
    public void updateRequestFormStatus(
            @PathVariable Long requestFormId,
            @RequestBody UpdateStatusRequest request
    ) {
        requestFormService.updateStatus(requestFormId, request.status());
    }
}
