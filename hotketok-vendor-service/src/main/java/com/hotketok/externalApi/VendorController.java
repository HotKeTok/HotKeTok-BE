package com.hotketok.externalApi;

import com.hotketok.dto.*;
import com.hotketok.dto.internalApi.VendorInfoResponse;
import com.hotketok.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vendor-service")
public class VendorController {
    private final VendorService vendorService;

    // 공사업체 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RegisterVendorResponse registerVendor(@RequestHeader("userId") Long vendorId,
                                                         @RequestPart("images") List<MultipartFile> images,
                                                         @RequestPart("file") MultipartFile file,
                                                         @RequestPart("data") RegisterVendorRequest request) {
        return vendorService.registerVendor(vendorId, images, file, request);
    }

    // 관리자 승인
    @PostMapping("/admin-approve/{vendorId}")
    public ResponseEntity<Void> approveVendor(@PathVariable Long vendorId) {
        vendorService.approveVendor(vendorId);
        return ResponseEntity.ok().build();
    }

    // 관리자 거절
    @DeleteMapping("/admin-reject/{vendorId}")
    public ResponseEntity<Void> adminReject(@PathVariable Long vendorId) {
        vendorService.rejectVendor(vendorId);
        return ResponseEntity.ok().build();
    }

    // 업체 정보 확인 (토큰 사용 x)
    @GetMapping("/profile")
    public VendorInfoAllResponse getProfile(@RequestParam Long vendorId) {
        return vendorService.getProfile(vendorId);
    }

    // 업체 정보 확인 (토큰 사용 o)
    @GetMapping("/profile/my")
    public VendorInfoAllResponse getProfileMypage(@RequestHeader("userId") Long userId) {
        return vendorService.getProfileMypage(userId);
    }

    // 업체 프로필 관리
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateProfile(
            @RequestHeader("userId") Long userId,
            @RequestPart("request") UpdateVendorProfileRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "introductionImages", required = false) List<MultipartFile> introductionImages
    ) {
        vendorService.updateProfile(userId, request, profileImage, introductionImages);
    }

    // 업체 소식 확인 (토큰 사용 x)
    @GetMapping("/news")
    public List<VendorNewsResponse> getVendorNews(@RequestParam Long vendorId) {
        return vendorService.getVendorNews(vendorId);
    }

    // 업체 소식 작성
    @PostMapping("/news")
    public void postNews(@RequestHeader("userId") Long userId, @RequestBody PostNewsRequest request) {
        vendorService.postNews(userId, request);
    }

    // 업체 소식 수정
    @PatchMapping("/news")
    public void patchNews(@RequestHeader("userId") Long userId, @RequestBody PatchNewsRequest request) {
        vendorService.patchNews(userId, request);
    }

    // 업체 소식 삭제
    @DeleteMapping("/news")
    public void deleteNews(@RequestHeader("userId") Long userId, @RequestParam Long newsId) {
        vendorService.deleteNews(userId, newsId);
    }

    // 보낸 견적서 조회
    @GetMapping("/estimate")
    public VendorEstimateListResponse getMyEstimates(@RequestHeader("userId") Long userId) {
        return vendorService.getMyEstimates(userId);
    }

    // 진행 중인 수리 조회
    @GetMapping("/processing")
    public MatchingEstimateListResponse getMatchingEstimates(@RequestHeader("userId") Long userId) {
        return vendorService.getMatchingEstimates(userId);
    }

    // 처리 완료 수리 조회
    @GetMapping("/done")
    public VendorEstimateListResponse getDoneEstimates(@RequestHeader("userId") Long userId) {
        return vendorService.getCompletedEstimates(userId);
    }

    // 수리 상세 조회
    @GetMapping("/detail")
    public EstimateDetailResponse getEstimateDetail(@RequestHeader("userId") Long userId, @RequestParam Long estimateId) {
        return vendorService.getEstimateDetail(userId, estimateId);
    }

    // 받은 수리 요청 조회
    @GetMapping("/request")
    public NewRequestListResponse getNewRequests(@RequestHeader("userId") Long userId) {
        return vendorService.getNewRequests(userId);
    }

    // 받은 수리 요청 상세 조회
    @GetMapping("/request-detail")
    public RequestDetailResponse getRequestFormDetail(@RequestHeader("userId") Long userId, @RequestParam Long requestId) {
        return vendorService.getRequestFormDetail(userId, requestId);
    }

    // 수리 개수 조회
    @GetMapping("/dashboard")
    public RequestCountResponse getRequestCounts(@RequestHeader("userId") Long userId) {
        return vendorService.getRequestCounts(userId);
    }

    // 수리 일정 캘린더
    @GetMapping("/calendar")
    public CalendarResponse getCalendarData(@RequestHeader("userId") Long userId, @RequestParam Integer year, @RequestParam Integer month) {
        return vendorService.getCalendarData(userId, year, month);
    }

    // 특정 날짜 일정 조회
    @GetMapping("/day")
    public DailyScheduleResponse getDailySchedule(@RequestHeader("userId") Long userId, @RequestParam Integer year, @RequestParam Integer month, @RequestParam Integer day) {
        return vendorService.getDailySchedule(userId, year, month, day);
    }
}
