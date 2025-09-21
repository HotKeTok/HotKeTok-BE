package com.hotketok.externalApi;

import com.hotketok.dto.internalApi.DeleteImageRequest;
import com.hotketok.dto.internalApi.DeleteImageResponse;
import com.hotketok.dto.internalApi.UploadImageResponse;
import com.hotketok.internalApi.InfraServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExampleController {

    private final InfraServiceClient infraServiceClient;


    @PostMapping("/example-service/upload")
    public UploadImageResponse uploadImages(@RequestPart(value = "images", required = true) List<MultipartFile> images) {
        // FeignClient를 통해 infra-service의 API를 호출
        return infraServiceClient.uploadImages(images, "requestform-image/");
    }

    @DeleteMapping("/example-service/delete")
    public DeleteImageResponse deleteImage(@RequestBody DeleteImageRequest deleteImageRequest) {
        return infraServiceClient.deleteImage(deleteImageRequest);
    }
}
