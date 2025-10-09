package com.hotketok.externalApi;

import com.hotketok.dto.*;
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
    public RegisterVendorResponse registerHouse(@RequestHeader("userId") Long vendorId,
                                                         @RequestPart("image") MultipartFile image,
                                                         @RequestPart("file") MultipartFile file,
                                                         @RequestPart("data") RegisterVendorRequest request) {
        return vendorService.registerVendor(vendorId, image, file, request);
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

    // 업체 프로필 관리
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateProfile(
            @RequestPart("request") UpdateVendorProfileRequest request,
            @RequestPart(value = "introductionImages", required = false) List<MultipartFile> introductionImages
    ) {
        Long userId = 103L;
        vendorService.updateProfile(userId, request, introductionImages);
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
}
