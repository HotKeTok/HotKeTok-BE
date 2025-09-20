package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.DeleteImageRequest;
import com.hotketok.dto.internalApi.DeleteImageResponse;
import com.hotketok.dto.internalApi.UploadImageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "infra-service", url = "${client.infra-service.url}")
public interface InfraServiceClient {

    @PostMapping(value = "/internal/infra-service/upload/imageList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UploadImageResponse uploadImages(
            @RequestPart(value = "images", required = true) List<MultipartFile> images,
            @RequestParam String folderName);

    @DeleteMapping(value = "/internal/infra-service/delete")
    DeleteImageResponse deleteImage(@RequestBody DeleteImageRequest deleteImageRequest);
}
