package com.hotketok.service;

import com.hotketok.domain.News;
import com.hotketok.domain.RunningTime;
import com.hotketok.domain.Vendor;
import com.hotketok.domain.VendorIntroductionImage;
import com.hotketok.domain.enums.Role;
import com.hotketok.domain.enums.Status;
import com.hotketok.domain.enums.VendorState;
import com.hotketok.dto.*;
import com.hotketok.dto.RequestFormDateResponse;
import com.hotketok.dto.UploadFileListResponse;
import com.hotketok.dto.internalApi.*;
import com.hotketok.dto.RegisterVendorRequest;
import com.hotketok.dto.RegisterVendorResponse;
import com.hotketok.dto.internalApi.UploadFileResponse;
import com.hotketok.dto.internalApi.VendorInfoResponse;
import com.hotketok.exception.VendorErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.*;
import com.hotketok.repository.NewsRepository;
import com.hotketok.repository.VendorIntroductionImageRepository;
import com.hotketok.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final VendorIntroductionImageRepository imageRepository;
    private final InfraServiceClient infraServiceClient;
    private final UserServiceClient userServiceClient;
    private final NewsRepository newsRepository;
    private final EstimateServiceClient estimateServiceClient;
    private final RequestFormServiceClient requestFormServiceClient;
    private final ReviewServiceClient reviewServiceClient;


    // 등록 전 공사업체 정보 조회
    @Transactional(readOnly = true)
    public BeforeRegisterVendorInfoResponse getBeforeRegisterVendorInfo(Long userId){
        Vendor vendor = vendorRepository.findByUserId(userId).orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));
        return BeforeRegisterVendorInfoResponse.from(vendor);
    }

    // 공사업체 등록 (state=0)
    @Transactional
    public RegisterVendorResponse registerVendor(Long userId, List<MultipartFile> images, MultipartFile file, RegisterVendorRequest request) {
        if (images.size() == 0) { // 소개사진 필수
            throw new CustomException(VendorErrorCode.NEED_INTRODUCTION_IMAGE);
        }
        Vendor vendor = vendorRepository.findByNameAndAddress(request.name(), request.address())
                .orElseGet(() -> {
                    UploadFileListResponse imageUrls = infraServiceClient.uploadImages(images, "vendor-introduction/");

                    // log.info(imageUrls.urls().get(0));
                    UploadFileResponse proveFile = infraServiceClient.uploadFile(file, "proveVendor/");

                    Vendor newVendor = Vendor.createVendor(
                            userId,
                            request.name(),
                            request.category(),
                            request.address(),
                            request.detailAddress(),
                            request.introduction(),
                            proveFile.fileUrl()
                    );

                    List<VendorIntroductionImage> introductionImages = imageUrls.fileList().stream().map(imageUrl -> {
                        return imageRepository.save(VendorIntroductionImage.builder().imageUrl(imageUrl).build());
                    }).toList();

                    for (VendorIntroductionImage image : introductionImages) {
                        newVendor.addIntroductionImage(image);
                    }

                    return vendorRepository.save(newVendor); // newVendor 저장 후 리턴
                });
        userServiceClient.updateOnboardingStageFlag(userId, true);
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
        userServiceClient.updateOnboardingStageFlag(vendor.getUserId(), false);
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

    // 업체 정보 확인 (토큰 사용 x)
    public VendorInfoAllResponse getProfile(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        int reviewCount = 0;
        double averageRate = 0.0;
        try {
            ReviewStatsResponse stats = reviewServiceClient.getReviewStatsByVendorId(vendorId);
            reviewCount = stats.reviewCount();
            averageRate = stats.averageRate();
        } catch (Exception e) {
            log.error("Failed to fetch review count for vendorId {}: {}", vendorId, e.getMessage());
        }

        return VendorInfoAllResponse.from(vendor, reviewCount, averageRate);
    }

    // 업체 정보 확인 (토큰 사용 o)
    public VendorInfoAllResponse getProfileMypage(Long userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        int reviewCount = 0;
        double averageRate = 0.0;
        try {
            ReviewStatsResponse stats = reviewServiceClient.getReviewStatsByVendorId(vendor.getId());
            reviewCount = stats.reviewCount();
            averageRate = stats.averageRate();
        } catch (Exception e) {
            log.error("Failed to fetch review count for vendorId {}: {}", vendor.getId(), e.getMessage());
        }

        return VendorInfoAllResponse.from(vendor, reviewCount, averageRate);
    }

    // 업체 프로필 관리
    @Transactional
    public void updateProfile(Long userId, UpdateVendorProfileRequest request, MultipartFile profileImage, List<MultipartFile> newImages) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        String oldProfileImageUrl = vendor.getImage();
//        List<String> oldImageUrls = vendor.getIntroductionImages().stream()
//                .map(VendorIntroductionImage::getImageUrl)
//                .toList();

        // 프로필 이미지
        String newProfileImageUrl = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            UploadFileListResponse response = infraServiceClient.uploadImages(List.of(profileImage), "vendor-profile/");
            if (response != null && response.fileList() != null && !response.fileList().isEmpty()) {
                newProfileImageUrl = response.fileList().get(0);
            }
        }

        // 소개 이미지
        List<String> newImageUrls = null;

        if (newImages != null) {
            newImageUrls = new ArrayList<>();
            if (!newImages.isEmpty()) {
                UploadFileListResponse response = infraServiceClient.uploadImages(newImages, "vendor-introduction/");
                newImageUrls = response.fileList();
            }
        }

        RunningTime newRunningTime = null;
        if (request.runningTime() != null) {
            RunningTimeRequest dto = request.runningTime();
            newRunningTime = new RunningTime(
                    dto.openingTime(),
                    dto.closingTime(),
                    dto.workingDayOfWeek()
            );
        }

        vendor.updateProfile(
                request.introduction(),
                request.phoneNumber(),
                newRunningTime,
                newProfileImageUrl,
                newImageUrls
        );

        // 보상 트랜잭션
        // 프로필 이미지 (null 아닌 경우)
        if (newProfileImageUrl != null && StringUtils.hasText(oldProfileImageUrl)) {
            infraServiceClient.deleteFile(new DeleteFileRequest(oldProfileImageUrl));
        }

        // 소개 이미지 (null 아닌 경우)
//        if (newImageUrls != null && !newImageUrls.isEmpty()) {
//            if (oldImageUrls != null && !oldImageUrls.isEmpty()) {
//
//                oldImageUrls.stream()
//                        .filter(StringUtils::hasText)
//                        .forEach(url -> infraServiceClient.deleteFile(new DeleteFileRequest(url)));
//            }
//        }
    }

    // 업체 관련 사진 삭제
    @Transactional
    public void deleteImages(Long userId, DeleteImagesRequest request) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND)); // 404 응답

        // 프로필 이미지 삭제
        String profileUrlToDelete = request.profileImage();

        // 요청이 있는 경우 -> url 같은 것 비교
        if (StringUtils.hasText(profileUrlToDelete) && profileUrlToDelete.equals(vendor.getImage())) {

            vendor.setProfileImage(null); // 이미지 없는 상태

            // 보상 트랜잭션
            infraServiceClient.deleteFile(new DeleteFileRequest(profileUrlToDelete));
        }

        // 소개 이미지 삭제
        List<String> introUrlsToDelete = request.introductionImages();

        // 요청이 있는 경우 -> url 같은 것 비교
        if (introUrlsToDelete != null && !introUrlsToDelete.isEmpty()) {

            List<VendorIntroductionImage> imagesToRemove = vendor.getIntroductionImages().stream()
                    .filter(img -> introUrlsToDelete.contains(img.getImageUrl()))
                    .toList();

            vendor.getIntroductionImages().removeAll(imagesToRemove);

            // 보상 트랜잭션
            imagesToRemove.stream()
                    .map(VendorIntroductionImage::getImageUrl)
                    .filter(StringUtils::hasText) 
                    .forEach(url -> infraServiceClient.deleteFile(new DeleteFileRequest(url)));
        }
    }

    // 업체 소식 확인
    public List<VendorNewsResponse> getVendorNews(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        List<News> newses = newsRepository.findByVendorId(vendorId);

        return newses.stream()
                .map(news -> {
                    VendorInfoAllResponse vendorProfile = VendorInfoAllResponse.from(vendor, 0, 0.0);
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

    // 업체 소식 수정
    @Transactional
    public void patchNews(Long userId, PatchNewsRequest request) {
        Long newsId = request.newsId();
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.NEWS_NOT_FOUND));

        if (!news.getVendor().getUserId().equals(userId)) {
            throw new CustomException(VendorErrorCode.NO_AUTHORITY_TO_DELETE_NEWS);
        }
        news.updateNews(request.title(), request.content());
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

        List<Status> activeStatuses = List.of(Status.SEARCHING);
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
                formData.estimateTime(),
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

    // 수리 일정 캘린더
    public CalendarResponse getCalendarData(Long userId, int year, int month) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        // 공사업체가 보낸 것 중 'MATCHING'인 견적서만 조회
        List<SimpleEstimateResponse> matchingEstimates = estimateServiceClient.getEstimateInfoByStatus(vendor.getId(), Status.MATCHING);
        if (matchingEstimates.isEmpty()) {
            return new CalendarResponse(year, month, Collections.emptyMap());
        }

        // 요청서 정보 가져옴
        List<Long> requestFormIds = matchingEstimates.stream().map(SimpleEstimateResponse::requestFormId).toList();
        ScheduledRequest scheduledRequest = new ScheduledRequest(requestFormIds, year, month);
        Map<Long, RequestFormDateResponse> scheduledFormsMap = requestFormServiceClient.getScheduledRequestForms(scheduledRequest).stream()
                .collect(Collectors.toMap(RequestFormDateResponse::requestId, form -> form));

        Map<String, List<CalendarItemResponse>> calendarData = matchingEstimates.stream()
                .filter(estimate -> scheduledFormsMap.containsKey(estimate.requestFormId())) // 해당 월에 스케줄이 있는 것만 필터링
                .map(estimate -> {
                    RequestFormDateResponse form = scheduledFormsMap.get(estimate.requestFormId());
                    // 견적서 id 기준으로 매핑
                    return new AbstractMap.SimpleEntry<>(
                            form.estimateTime().toLocalDate().toString(),
                            new CalendarItemResponse(estimate.estimateId(), form.category())
                    );
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        return new CalendarResponse(year, month, calendarData);
    }

    // 특정 날짜 일정 조회
    public DailyScheduleResponse getDailySchedule(Long userId, Integer year, Integer month, Integer day) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        // MATCHING 상태인 것만 가져옴
        List<SimpleEstimateResponse> allEstimates = estimateServiceClient.getEstimateInfoByStatus(vendor.getId(), Status.MATCHING);

        if (allEstimates.isEmpty()) {
            return new DailyScheduleResponse(year, month, day, 0, Collections.emptyList());
        }

        List<Long> allRequestFormIds = allEstimates.stream().map(SimpleEstimateResponse::requestFormId).toList();
        var scheduledRequest = new ScheduledOnDateRequest(allRequestFormIds, year, month, day);
        Map<Long, RequestFormDetailResponse> scheduledFormsMap = requestFormServiceClient.getScheduledRequestFormsOnDate(scheduledRequest).stream()
                .collect(Collectors.toMap(RequestFormDetailResponse::requestFormId, form -> form));

        List<Long> payerIds = scheduledFormsMap.values().stream().map(RequestFormDetailResponse::payerId).distinct().toList();
        Map<Long, UserInfoDetailResponse> userInfoMap = userServiceClient.getUserInfosByIds(payerIds).stream()
                .collect(Collectors.toMap(UserInfoDetailResponse::userId, info -> info));

        List<DailyScheduleItem> items = allEstimates.stream()
                .filter(estimate -> scheduledFormsMap.containsKey(estimate.requestFormId()))
                .map(estimate -> {
                    RequestFormDetailResponse formData = scheduledFormsMap.get(estimate.requestFormId());
                    UserInfoDetailResponse payerInfo = userInfoMap.get(formData.payerId());

                    // null 방지 (회원탈퇴한 사용자의 경우 에러 방어)
                    String payerName = "(알 수 없는 사용자)";
                    String phoneNumber = null;

                    if (payerInfo != null) {
                        payerName = payerInfo.name();
                        phoneNumber = payerInfo.phoneNumber();
                    }

                    return new DailyScheduleItem(
                            estimate.estimateId(),
                            formData.category(),
                            formData.address(),
                            estimate.estimateTime(),
                            estimate.estimatePrice(),
                            formData.payType(),
                            payerName,
                            phoneNumber,
                            estimate.estimateComment(),
                            estimate.status()
                    );
                })
                .collect(Collectors.toList());

        return new DailyScheduleResponse(year, month, day, items.size(), items);
    }

    // 카테고리 조회
    public List<VendorCategoryResponse> findCategoriesByVendorIds(List<Long> vendorIds) {
        List<Vendor> vendors = vendorRepository.findAllById(vendorIds);

        return vendors.stream()
                .map(vendor -> new VendorCategoryResponse(vendor.getId(), vendor.getCategory()))
                .collect(Collectors.toList());
    }

    // 공사업체 (다수) 프로필 조회
    public List<VendorProfileResponse> getVendorProfilesByIds(List<Long> vendorIds) {
        if (vendorIds == null || vendorIds.isEmpty()) {
            return List.of();
        }

        return vendorRepository.findAllById(vendorIds).stream()
                .map(vendor -> new VendorProfileResponse(
                        vendor.getId(),
                        vendor.getName(),
                        vendor.getImage()
                ))
                .collect(Collectors.toList());
    }

    // 단일 유저 아이디로 공사업체 정보 조회
    public VendorInfoResponse findVendorInfoByUserId(Long userId) {
        Vendor vendor = vendorRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(VendorErrorCode.VENDOR_NOT_FOUND));

        return VendorInfoResponse.from(vendor);
    }
}
