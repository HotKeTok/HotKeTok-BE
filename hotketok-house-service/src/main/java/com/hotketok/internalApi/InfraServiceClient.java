package com.hotketok.internalApi;

import com.hotketok.dto.internalApi.DeleteFileRequest;
import com.hotketok.dto.internalApi.DeleteFileResponse;
import com.hotketok.dto.internalApi.UploadFileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@FeignClient(name = "infra-service", url = "${client.infra-service.url}")
public interface InfraServiceClient {

    @PostMapping(value = "/internal/infra-service/upload/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UploadFileResponse uploadFile(
            @RequestPart(value = "file") MultipartFile file,
            @RequestParam String folderName);

    @DeleteMapping(value = "/internal/infra-service/delete")
    DeleteFileResponse deleteFile(@RequestBody DeleteFileRequest deleteImageRequest);
}

