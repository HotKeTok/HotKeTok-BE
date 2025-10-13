package com.hotketok.service;

import com.hotketok.domain.News;
import com.hotketok.domain.Vendor;
import com.hotketok.domain.VendorIntroductionImage;
import com.hotketok.domain.enums.Role;
import com.hotketok.domain.enums.Status;
import com.hotketok.domain.enums.VendorState;
import com.hotketok.dto.*;
import com.hotketok.dto.UploadFileListResponse;
import com.hotketok.dto.internalApi.*;
import com.hotketok.dto.RegisterVendorRequest;
import com.hotketok.dto.RegisterVendorResponse;
import com.hotketok.dto.internalApi.UploadFileResponse;
import com.hotketok.dto.internalApi.VendorInfoResponse;
import com.hotketok.exception.VendorErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.EstimateServiceClient;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.internalApi.RequestFormServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.NewsRepository;
import com.hotketok.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final InfraServiceClient infraServiceClient;
    private final UserServiceClient userServiceClient;
    private final NewsRepository newsRepository;
    private final EstimateServiceClient estimateServiceClient;
    private final RequestFormServiceClient requestFormServiceClient;

    // 공사업체 등록 (state=0)
    @Transactional
    public RegisterVendorResponse registerVendor(Long userId, MultipartFile image, MultipartFile file, RegisterVendorRequest request) {

        Vendor vendor = vendorRepository.findByNameAndAddress(request.name(), request.address())
                .orElseGet(() -> {
                    UploadFileResponse imageFile = infraServiceClient.uploadFile(image, "VendorImage/");
                    UploadFileResponse proveFile = infraServiceClient.uploadFile(file, "proveVendor/");

                    Vendor newVendor = Vendor.createVendor(
                            userId,
                            request.name(),
                            request.category(),
                            request.address(),
                            request.detailAddress(),
                            request.introduction(),
                            imageFile.fileUrl(),
                            proveFile.fileUrl()
                    );

                    return vendorRepository.save(newVendor); // newVendor 저장 후 리턴
                });

        return new RegisterVendorResponse(vendor.getId());
    }

    // 관리자 승인 → VENDOR로 승격
    @Transactional
    public void approveVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));
        if (vendor.getState().equals(VendorState.REGISTERED)) throw new CustomException(VendorErrorCode.ALREADY_REGISTERED);
        vendor.changeState(VendorState.REGISTERED);
        userServiceClient.updateRole(vendor.getUserId(), Role.VENDOR);
    }

    // 관리자 거절 -> 삭제
    @Transactional
    public void rejectVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));
        if (vendor.getState().equals(VendorState.REGISTERED)) throw new CustomException(VendorErrorCode.ALREADY_REGISTERED);
        vendorRepository.deleteById(vendorId);
    }

    // 공사업체 정보 반환
    public List<VendorInfoResponse> findVendorInfosByIds(List<Long> vendorIds) {
        return vendorRepository.findAllByIdIn(vendorIds).stream()
                .map(VendorInfoResponse::from)
                .collect(Collectors.toList());
    }

    // 단일 공사업체 정보 조회
    public VendorInfoResponse findVendorInfoById(Long vendorId) {
        return vendorRepository.findById(vendorId)
                .map(VendorInfoResponse::from)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));
    }

    // 업체 정보 확인
    public VendorInfoAllResponse getProfile(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));
        return VendorInfoAllResponse.from(vendor);
    }

    // 업체 프로필 관리
    @Transactional
    public void updateProfile(Long userId, UpdateVendorProfileRequest request, List<MultipartFile> newImages) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        // 기존 소개 이미지 목록 조회
        List<String> oldImageUrls = vendor.getIntroductionImages().stream()
                .map(VendorIntroductionImage::getImageUrl)
                .toList();

        List<String> newImageUrls = new ArrayList<>();
        if (newImages != null && !newImages.isEmpty()) {
            UploadFileListResponse response = infraServiceClient.uploadImages(newImages, "vendor-introduction/");
            newImageUrls = response.urls();
        }

        vendor.updateProfile(
                request.introduction(),
                request.phoneNumber(),
                request.runningTime(),
                request.profileImage(),
                newImageUrls
        );

        // 보상 트랜잭션
        if (!oldImageUrls.isEmpty()) {
            oldImageUrls.forEach(url -> infraServiceClient.deleteFile(new DeleteFileRequest(url)));
        }
    }

    // 업체 소식 확인
    public List<VendorNewsResponse> getVendorNews(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        List<News> newses = newsRepository.findByVendorId(vendorId);

        return newses.stream()
                .map(news -> {
                    VendorInfoAllResponse vendorProfile = VendorInfoAllResponse.from(vendor);
                    return VendorNewsResponse.of(news, vendorProfile);
                })
                .collect(Collectors.toList());
    }

    // 업체 소식 작성
    @Transactional
    public void postNews(Long userId, PostNewsRequest request) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));
        News newNews = News.createNews(request.title(), request.content());
        vendor.addNews(newNews);
    }

    // 업체 소식 삭제
    @Transactional
    public void deleteNews(Long userId, Long newsId) {
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.NEWS_NOT_FOUND));

        // 작성자 소유 확인
        if (!news.getVendor().getUserId().equals(userId)) {
            throw new CustomException(VendorErrorCode.NO_AUTHORITY_TO_DELETE_NEWS);
        }
        newsRepository.delete(news);
    }

    // 보낸 견적서 조회
    public VendorEstimateListResponse getMyEstimates(Long userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));
        Long vendorId = vendor.getId();

        // 보낸 견적서 정보 받아옴
        List<EstimateInfoResponse> estimates = estimateServiceClient.getEstimatesByVendorId(vendorId);
        if (estimates.isEmpty()) {
            return new VendorEstimateListResponse(0, Collections.emptyList());
        }

        // 견적서에서 요청서 id 모음
        List<Long> requestFormIds = estimates.stream()
                .map(EstimateInfoResponse::requestFormId)
                .distinct()
                .toList();

        // 요청서 정보(주소, 카테고리) 가져옴
        Map<Long, RequestFormDetailResponse> requestFormMap = requestFormServiceClient.getRequestFormsByIds(requestFormIds).stream()
                .collect(Collectors.toMap(RequestFormDetailResponse::requestFormId, data -> data));

        List<VendorEstimateResponse> resultList = estimates.stream()
                .map(estimate -> {
                    RequestFormDetailResponse formData = requestFormMap.get(estimate.requestFormId());
                    return new VendorEstimateResponse(
                            estimate.estimateId(),
                            formData.category(),
                            formData.address(),
                            estimate.estimateTime(),
                            estimate.status()
                    );
                })
                .collect(Collectors.toList());

        return new VendorEstimateListResponse(resultList.size(), resultList);
    }

    // 진행 중인 수리 조회
    public MatchingEstimateListResponse getMatchingEstimates(Long userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        // 'MATCHING' 상태인 견적서 목록 조회
        List<EstimateInfoResponse> matchingEstimates = estimateServiceClient.getMatchingEstimatesByVendorId(vendor.getId());
        if (matchingEstimates.isEmpty()) {
            return new MatchingEstimateListResponse(0, Collections.emptyList());
        }

        List<Long> requestFormIds = matchingEstimates.stream().map(EstimateInfoResponse::requestFormId).distinct().toList();

        Map<Long, RequestFormDetailResponse> requestFormMap = requestFormServiceClient.getRequestFormsByIds(requestFormIds).stream()
                .collect(Collectors.toMap(RequestFormDetailResponse::requestFormId, data -> data));

        // 비용 부담 주체 정보 가져옴
        List<Long> payerIds = requestFormMap.values().stream().map(RequestFormDetailResponse::payerId).distinct().toList();

        log.info(">>> Calling user-service with payerIds: {}", payerIds);

        Map<Long, UserInfoDetailResponse> userInfoMap = userServiceClient.getUserInfosByIds(payerIds).stream()
                .collect(Collectors.toMap(UserInfoDetailResponse::userId, info -> info));

        log.info("<<< Received userInfoMap from user-service: {}", userInfoMap);

        List<MatchingEstimateInfoResponse> items = matchingEstimates.stream().map(estimate -> {
            RequestFormDetailResponse formData = requestFormMap.get(estimate.requestFormId());
            UserInfoDetailResponse payerInfo = userInfoMap.get(formData.payerId());

            String payerName = "(알 수 없는 사용자)";
            String phoneNumber = null;
            if (payerInfo != null) {
                payerName = payerInfo.name();
                phoneNumber = payerInfo.phoneNumber();
            }

            return new MatchingEstimateInfoResponse(
                    estimate.estimateId(),
                    formData.category(),
                    formData.address(),
                    estimate.estimateTime(),
                    estimate.estimatePrice(),
                    formData.payType(),
                    //payerInfo.name(),
                    payerName,
                    //payerInfo.phoneNumber(),
                    phoneNumber,
                    estimate.estimateComment()
            );
        }).collect(Collectors.toList());

        return new MatchingEstimateListResponse(items.size(), items);
    }

    // 처리 완료 수리 조회
    public VendorEstimateListResponse getCompletedEstimates(Long userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        List<EstimateInfoResponse> completedEstimates = estimateServiceClient.getCompletedEstimatesByVendorId(vendor.getId());
        if (completedEstimates.isEmpty()) {
            return new VendorEstimateListResponse(0, Collections.emptyList());
        }

        List<Long> requestFormIds = completedEstimates.stream().map(EstimateInfoResponse::requestFormId).distinct().toList();
        Map<Long, RequestFormDetailResponse> requestFormMap = requestFormServiceClient.getRequestFormsByIds(requestFormIds).stream()
                .collect(Collectors.toMap(RequestFormDetailResponse::requestFormId, data -> data));

        List<VendorEstimateResponse> items = completedEstimates.stream().map(estimate -> {
            RequestFormDetailResponse formData = requestFormMap.get(estimate.requestFormId());
            return new VendorEstimateResponse(
                    estimate.estimateId(),
                    formData.category(),
                    formData.address(),
                    estimate.estimateTime(),
                    estimate.status()
            );
        }).collect(Collectors.toList());

        return new VendorEstimateListResponse(items.size(), items);
    }

    // 수리 상세 조회
    public EstimateDetailResponse getEstimateDetail(Long userId, Long estimateId) {
        SimpleEstimateResponse estimate = estimateServiceClient.getEstimateById(estimateId);

        // 확인 권한 확인
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));
        if (!vendor.getId().equals(estimate.vendorId())) {
            throw new CustomException(VendorErrorCode.NO_AUTHORITY);
        }

        RequestFormDetailResponse formData = requestFormServiceClient.getRequestFormDetail(estimate.requestFormId());
        UserInfoDetailResponse payerInfo = userServiceClient.getUserInfoById(formData.payerId());

        return new EstimateDetailResponse(
                estimate.estimateId(),
                formData.category(),
                formData.address(),
                estimate.estimateTime(),
                estimate.estimatePrice(),
                formData.payType(),
                payerInfo.name(),
                payerInfo.phoneNumber(),
                formData.requestImages(),
                formData.requestDescription(),
                estimate.estimateComment(),
                estimate.status()
        );
    }

    // 받은 수리 요청 조회
    public NewRequestListResponse getNewRequests(Long userId) {
        // 권한 확인 제외
        // 추후 요청서를 받은 로직이 추가 / 제외 될 수 있기에 일단 userId는 받는 걸로 설정

        List<Status> activeStatuses = List.of(Status.SEARCHING, Status.CHOOSING);
        List<RequestFormSimpleResponse> requests = requestFormServiceClient.getRequestFormsByStatus(activeStatuses);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd / a hh:mm", Locale.KOREAN);

        List<NewRequestItem> items = requests.stream()
                .map(req -> new NewRequestItem(
                        req.requestId(),
                        req.category(),
                        req.address(),
                        req.estimateTime().format(formatter) // 시간 포맷 변경
                ))
                .collect(Collectors.toList());

        return new NewRequestListResponse(items.size(), items);
    }

    // 받은 수리 요청 상세 조회
    public RequestDetailResponse getRequestFormDetail(Long userId, Long requestId) {
        // 권한 확인 제외
        // 추후 요청서를 받은 로직이 추가 / 제외 될 수 있기에 일단 userId는 받는 걸로 설정

        RequestFormDetailResponse formData = requestFormServiceClient.getRequestFormDetail(requestId);
        UserInfoDetailResponse payerInfo = userServiceClient.getUserInfoById(formData.payerId());

        return new RequestDetailResponse(
                formData.category(),
                formData.address(),
                formData.requestSchedule(),
                formData.payType(),
                payerInfo.name(),
                payerInfo.phoneNumber(),
                formData.requestDescription(),
                formData.requestImages()
        );
    }

    // 수리 개수 조회
    public RequestCountResponse getRequestCounts(Long userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        List<Status> activeStatuses = List.of(Status.SEARCHING, Status.CHOOSING);
        List<RequestFormSimpleResponse> requests = requestFormServiceClient.getRequestFormsByStatus(activeStatuses);

        EstimateStatusCountResponse otherCounts = estimateServiceClient.getEstimateCounts(vendor.getId());

        return new RequestCountResponse(
                requests.size(),
                otherCounts.processingRequest(),
                otherCounts.doneRequest()
        );
    }
}
