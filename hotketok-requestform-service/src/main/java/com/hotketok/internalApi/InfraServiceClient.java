package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.DeleteFileRequest;
import com.hotketok.dto.internalApi.DeleteFileResponse;
import com.hotketok.dto.internalApi.UploadFileListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "infra-service", url = "${client.infra-service.url}")
public interface InfraServiceClient {

    @PostMapping(value = "/internal/infra-service/upload/imageList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UploadFileListResponse uploadImages(
            @RequestPart(value = "images", required = true) List<MultipartFile> images,
            @RequestParam String folderName);

    @DeleteMapping(value = "/internal/infra-service/delete")
    DeleteFileResponse deleteFile(@RequestBody DeleteFileRequest deleteFileRequest);
}
