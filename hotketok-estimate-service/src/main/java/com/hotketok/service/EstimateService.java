package com.hotketok.service;

import com.hotketok.domain.Estimate;
import com.hotketok.dto.PostEstimateRequest;
import com.hotketok.dto.PostEstimateResponse;
import com.hotketok.dto.internalApi.RequestFormResponse;
import com.hotketok.internalApi.RequestFormClient;
import com.hotketok.repository.EstimateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstimateService {
    private final EstimateRepository estimateRepository;
    private final RequestFormClient requestFormClient;

    public PostEstimateResponse postEstimate(Long vendorId, PostEstimateRequest request) {
        RequestFormResponse requestFormData = requestFormClient.getRequestFormData(request.requestFormId());

        Estimate estimate = Estimate.createEstimate(
                request.requestFormId(),
                vendorId,
                request.estimatePrice(),
                request.decisionLater(),
                request.comment()
        );
        Estimate savedEstimate = estimateRepository.save(estimate);

        // 반환에는 주소, 카테고리 포함
        return PostEstimateResponse.from(
                savedEstimate,
                requestFormData.address(),
                requestFormData.category()
        );
    }
}
