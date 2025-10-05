package com.hotketok.service;

import com.hotketok.domain.Estimate;
import com.hotketok.dto.PostEstimateRequest;
import com.hotketok.dto.PostEstimateResponse;
import com.hotketok.dto.EstimateResponse;
import com.hotketok.dto.internalApi.RequestFormResponse;
import com.hotketok.dto.internalApi.VendorInfoResponse;
import com.hotketok.internalApi.RequestFormServiceClient;
import com.hotketok.internalApi.VendorServiceClient;
import com.hotketok.repository.EstimateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EstimateService {
    private final EstimateRepository estimateRepository;
    private final RequestFormServiceClient requestFormClient;
    private final VendorServiceClient vendorServiceClient;

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

    // 받은 견적서 조회
    @Transactional(readOnly = true)
    public List<EstimateResponse> getEstimatesByRequestFormId(Long requestFormId) {
        List<Estimate> estimates = estimateRepository.findAllByRequestFormId(requestFormId);

        if (estimates.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> vendorIds = estimates.stream()
                .map(Estimate::getVendorId)
                .distinct()
                .toList();

        List<VendorInfoResponse> vendorInfos = vendorServiceClient.getVendorInfosByIds(vendorIds);
        Map<Long, VendorInfoResponse> vendorInfoMap = vendorInfos.stream()
                .collect(Collectors.toMap(VendorInfoResponse::vendorId, info -> info));

        return estimates.stream()
                .map(estimate -> {
                    VendorInfoResponse vendorInfo = vendorInfoMap.get(estimate.getVendorId());
                    return EstimateResponse.from(estimate, vendorInfo);
                })
                .collect(Collectors.toList());
    }
}
