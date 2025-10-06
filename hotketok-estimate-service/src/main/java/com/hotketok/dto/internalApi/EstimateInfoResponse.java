package com.hotketok.dto.internalApi;
import com.hotketok.domain.Estimate;
import com.hotketok.domain.enums.Status;
import java.time.LocalDateTime;

public record EstimateInfoResponse(
        Long estimateId,
        Long requestFormId,
        LocalDateTime estimateTime,
        Status status
) {
    public static EstimateInfoResponse from(Estimate estimate) {
        return new EstimateInfoResponse(
                estimate.getId(),
                estimate.getRequestFormId(),
                estimate.getCreatedAt(),
                estimate.getStatus()
        );
    }
}