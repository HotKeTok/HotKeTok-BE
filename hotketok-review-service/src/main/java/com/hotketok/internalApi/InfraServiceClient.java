package com.hotketok.internalApi;

import com.hotketok.dto.UploadFileListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@FeignClient(name = "infra-service", url = "${client.infra-service.url}")
public interface InfraServiceClient {

    @PostMapping(value = "/internal/infra-service/upload/imageList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    UploadFileListResponse uploadImages(
            @RequestPart("images") List<MultipartFile> images,
            @RequestParam("folderName") String folderName
    );
}