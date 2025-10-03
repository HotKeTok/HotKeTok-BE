package com.hotketok.service;

import com.hotketok.domain.Vendor;
import com.hotketok.domain.enums.VendorState;
import com.hotketok.dto.RegisterVendorRequest;
import com.hotketok.dto.RegisterVendorResponse;
import com.hotketok.dto.internalApi.Role;
import com.hotketok.dto.internalApi.UploadFileResponse;
import com.hotketok.exception.VendorErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.InfraServiceClient;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@Slf4j
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final InfraServiceClient infraServiceClient;
    private final UserServiceClient userServiceClient;

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

}
