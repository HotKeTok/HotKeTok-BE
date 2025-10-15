package com.hotketok.internalApi;

import com.hotketok.domain.enums.Status;
import com.hotketok.dto.internalApi.*;
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
    public List<RequestFormDetailResponse> getRequestFormsByIds(@RequestBody List<Long> requestFormIds) {
        return requestFormService.getRequestFormsByIds(requestFormIds);
    }

    @GetMapping("/{requestFormId}/detail")
    public RequestFormDetailResponse getRequestFormDetail(@PathVariable Long requestFormId) {
        return requestFormService.getRequestFormDetailById(requestFormId);
    }

    // 다중 상태로 요청서 찾기
    @GetMapping("/by-status")
    public List<RequestFormSimpleResponse> getRequestFormsByStatus(@RequestParam List<Status> statuses) {
        return requestFormService.findRequestFormsByStatuses(statuses);
    }

    // 일정에 맞는 요청서 찾기
    @PostMapping("/scheduled-info")
    public List<RequestFormSimpleResponse> getScheduledRequestForms(@RequestBody ScheduledRequest request) {
        return requestFormService.findScheduledRequestForms(
                request.requestFormIds(), request.year(), request.month()
        );
    }

    // 해당 날짜 일정 조회
    @PostMapping("/scheduled-on-date")
    public List<RequestFormDetailResponse> getScheduledRequestFormsOnDate(@RequestBody ScheduledOnDateRequest request) {
        return requestFormService.findScheduledRequestFormsOnDate(
                request.requestFormIds(), request.year(), request.month(), request.day()
        );
    }
}
