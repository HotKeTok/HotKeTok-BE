package com.hotketok.externalApi;

import com.hotketok.dto.RegisterVendorRequest;
import com.hotketok.dto.RegisterVendorResponse;
import com.hotketok.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}
