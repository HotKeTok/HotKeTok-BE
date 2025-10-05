package com.hotketok.internalApi;

import com.hotketok.domain.RequestForm;
import com.hotketok.dto.internalApi.RequestFormResponse;
import com.hotketok.service.RequestFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/requestform-service")
@RequiredArgsConstructor
public class RequestFormInternalController {

    private final RequestFormService requestFormService;

    @GetMapping("/{requestFormId}")
    public RequestFormResponse getRequestFormData(@PathVariable Long requestFormId) {
        return requestFormService.getRequestFormDataById(requestFormId);
    }
}
