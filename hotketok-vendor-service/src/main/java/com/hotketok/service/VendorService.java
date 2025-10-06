package com.hotketok.service;

import com.hotketok.domain.News;
import com.hotketok.domain.Vendor;
import com.hotketok.domain.VendorIntroductionImage;
import com.hotketok.domain.enums.VendorState;
import com.hotketok.dto.*;
import com.hotketok.dto.internalApi.*;
import com.hotketok.exception.VendorErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.NewsRepository;
import com.hotketok.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final InfraServiceClient infraServiceClient;
    private final UserServiceClient userServiceClient;
    private final NewsRepository newsRepository;

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
}
