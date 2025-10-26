package com.hotketok.service;

import com.hotketok.domain.Estimate;
import com.hotketok.domain.enums.Status;
import com.hotketok.dto.PostEstimateRequest;
import com.hotketok.dto.PostEstimateResponse;
import com.hotketok.dto.EstimateResponse;
import com.hotketok.dto.internalApi.*;
import com.hotketok.dto.internalApi.RequestFormResponse;
import com.hotketok.dto.internalApi.UpdateStatusRequest;
import com.hotketok.dto.internalApi.VendorInfoResponse;
import com.hotketok.exception.EstimateErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
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
@Transactional
public class EstimateService {
    private final EstimateRepository estimateRepository;
    private final RequestFormServiceClient requestFormClient;
    private final VendorServiceClient vendorServiceClient;

    public PostEstimateResponse postEstimate(Long userId, PostEstimateRequest request) {
        RequestFormResponse requestFormData = requestFormClient.getRequestFormData(request.requestFormId());
        VendorInfoResponse vendorInfo = vendorServiceClient.getVendorInfoByUserId(userId);

        Estimate estimate = Estimate.createEstimate(
                request.requestFormId(),
                vendorInfo.vendorId(),
                request.estimatePrice(),
                request.decisionLater(),
                request.comment()
        );
        estimate.changeStatus(Status.SEARCHING);
        requestFormClient.updateRequestFormStatus(request.requestFormId(), new UpdateStatusRequest(Status.CHOOSING));
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

    // 견적서 선택
    @Transactional
    public void selectEstimate(Long userId, Long estimateId) {
        Estimate selectedEstimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new CustomException(EstimateErrorCode.ESTIMATE_NOT_FOUND));

        // 유저가 견적서 선택 권한있는지 확인
        Long requestFormId = selectedEstimate.getRequestFormId();

        RequestFormPayerResponse authorResponse = requestFormClient.getRequestFormAuthor(requestFormId);
        Long payerId = authorResponse.payerId();

        if (!payerId.equals(userId)) {
            throw new CustomException(EstimateErrorCode.NO_AUTHORITY_TO_SELECT);
        }

        selectedEstimate.changeStatus(Status.MATCHING);

        List<Estimate> otherEstimates = estimateRepository.findAllByRequestFormId(requestFormId);

        otherEstimates.forEach(estimate -> {
            if (!estimate.getId().equals(estimateId)) {
                estimate.changeStatus(Status.REJECTED);
            }
        });

        requestFormClient.updateRequestFormStatus(requestFormId, new UpdateStatusRequest(Status.MATCHING));
    }

    // 견적서 삭제
    @Transactional
    public void deleteEstimate(Long userId, Long estimateId) {
        Estimate estimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new CustomException(EstimateErrorCode.ESTIMATE_NOT_FOUND));

        // 유저의 삭제 권한 확인
        Long vendorId = estimate.getVendorId();

        VendorInfoResponse vendorInfo = vendorServiceClient.getVendorInfoById(vendorId);

        if (!vendorInfo.userId().equals(userId)) {
            throw new CustomException(EstimateErrorCode.NO_AUTHORITY_TO_DELETE);
        }

        estimateRepository.delete(estimate);
    }
    // 공사업체 id로 견적서 정보 반환
    public List<EstimateInfoResponse> findEstimatesByVendorId(Long vendorId) {
        return estimateRepository.findAllByVendorId(vendorId).stream()
                .map(EstimateInfoResponse::from)
                .collect(Collectors.toList());
    }

    // 매칭 상태인 견적서 조회
    public List<SimpleEstimateResponse> findEstimatesByVendorIdAndStatus(Long vendorId, Status status) {
        return estimateRepository.findAllByVendorIdAndStatus(vendorId, status).stream()
                .map(SimpleEstimateResponse::from)
                .collect(Collectors.toList());
    }

    // 견적서 정보 조회
    public SimpleEstimateResponse findSimpleEstimateById(Long estimateId) {
        return estimateRepository.findById(estimateId)
                .map(SimpleEstimateResponse::from)
                .orElseThrow(() -> new CustomException(EstimateErrorCode.ESTIMATE_NOT_FOUND));
    }

    // 특정 상태의 견적서 개수 조회
    public EstimateStatusCountResponse getEstimateCountsByVendorId(Long vendorId) {
        long processingCount = estimateRepository.countByVendorIdAndStatus(vendorId, Status.MATCHING);
        long doneCount = estimateRepository.countByVendorIdAndStatus(vendorId, Status.COMPLETED);

        return new EstimateStatusCountResponse(processingCount, doneCount);
    }

    // 특정 날짜 일정 조회
    public List<SimpleEstimateResponse> findSimpleEstimatesByVendorIdAndStatus(Long vendorId, Status status) {
        List<Estimate> estimates = estimateRepository.findAllByVendorIdAndStatus(vendorId, status);

        return estimates.stream()
                .map(SimpleEstimateResponse::from)
                .collect(Collectors.toList());
    }

    public boolean checkCompletedWork(Long userId, Long vendorId) {
        List<Long> requestFormIds = requestFormClient.getRequestFormIdsByAuthorId(userId);

        if (requestFormIds.isEmpty()) {
            return false;
        }
        return estimateRepository.existsByVendorIdAndStatusAndRequestFormIdIn(
                vendorId,
                Status.COMPLETED,
                requestFormIds
        );
    }

    // 요청서 id를 통한 견적서 금액 확인 (지난 수리요청서 조회 API를 위한 내부 API)
    public EstimatePriceResponse findEstimatePriceByRequestFormId(Long requestFormId) {
        Estimate estimate = estimateRepository.findByRequestFormId(requestFormId).orElseThrow(() -> new CustomException(EstimateErrorCode.ESTIMATE_NOT_FOUND));
        return new EstimatePriceResponse(estimate.getEstimatePrice());
    }

    // 선택한 견적서 내용 조회
    public EstimateResponse getEstimateInfo(Long userId, Long estimateId) {
        Estimate estimate = estimateRepository.findById(estimateId)
                .orElseThrow(() -> new CustomException(EstimateErrorCode.ESTIMATE_NOT_FOUND));

        Long vendorId = estimate.getVendorId();

        VendorInfoResponse vendorInfo = null;
        try {
            vendorInfo = vendorServiceClient.getVendorInfoById(vendorId);
        } catch (Exception e) {
            log.error("Failed to get vendor info for vendorId {}: {}", vendorId, e.getMessage());
        }

        // 상태 확인
        if (estimate.getStatus() != Status.MATCHING) {
            throw new CustomException(EstimateErrorCode.ESTIMATE_NOT_MATCHING);
        }

        return EstimateResponse.from(estimate, vendorInfo);
    }
}
