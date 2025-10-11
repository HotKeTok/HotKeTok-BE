package com.hotketok.dto;

import com.hotketok.domain.RequestForm;
import com.hotketok.domain.enums.ConstructCategory;
import com.hotketok.domain.enums.PayType;
import com.hotketok.domain.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

// 진행중인 수리 요청서 조회 API 응답값
public record InProgressRequestFormResponse(
        int count,
        List<InProgressRequestFormInfo> list
) {
    public record InProgressRequestFormInfo(
            Long requestFormId,
            ConstructCategory category,
            LocalDateTime requestSchedule,
            PayType payType,
            Status status,
            String number
    ){
    }

    public static InProgressRequestFormResponse fromOwner(List<RequestForm> requestForms) {
        List<InProgressRequestFormInfo> list = requestForms.stream()
                .map(requestForm -> new InProgressRequestFormInfo(
                        requestForm.getId(),
                        requestForm.getCategory(),
                        requestForm.getRequestSchedule(),
                        requestForm.getPayType(),
                        requestForm.getStatus(),
                        requestForm.getNumber()
                )).toList();
        return new InProgressRequestFormResponse(requestForms.size(),list);
    }

    public static InProgressRequestFormResponse fromTenant(List<RequestForm> requestForms) {
        List<InProgressRequestFormInfo> list = requestForms.stream()
                .map(requestForm -> new InProgressRequestFormInfo(
                        requestForm.getId(),
                        requestForm.getCategory(),
                        requestForm.getRequestSchedule(),
                        requestForm.getPayType(),
                        requestForm.getStatus(),
                        null
                )).toList();
        return new InProgressRequestFormResponse(requestForms.size(),list);
    }
}
