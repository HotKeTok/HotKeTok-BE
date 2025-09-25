package com.hotketok.internalApi;


import com.hotketok.dto.DeleteFileRequest;
import com.hotketok.dto.DeleteFileResponse;
import com.hotketok.dto.UploadFileListResponse;
import com.hotketok.dto.UploadFileResponse;
import com.hotketok.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/internal/infra-service")
public class ImageInternalController {

    private final ImageStorageService imageStorageService;

    @Autowired
    public ImageInternalController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/upload/imageList")
    public UploadFileListResponse uploadImages(
            @RequestPart(value = "images", required = true) List<MultipartFile> images,
            @RequestParam String folderName) throws IOException {
        List<String> urls = new ArrayList<>();

        for (MultipartFile image : images) {
            String publicUrl = imageStorageService.uploadImage(image,folderName);
            urls.add(publicUrl);
        }
        return new UploadFileListResponse(urls);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, value = "/upload/file")
    public UploadFileResponse uploadFile(
            @RequestPart(value = "file") MultipartFile file,
            @RequestParam String folderName) throws IOException {
        String publicUrl = imageStorageService.uploadImage(file,folderName);
        return new UploadFileResponse(publicUrl);
    }

    @DeleteMapping("/delete")
    public DeleteFileResponse deleteFile(@RequestBody DeleteFileRequest deleteFileRequest) throws IOException {
        imageStorageService.deleteImage(deleteFileRequest.deletedFileUrl());
        return new DeleteFileResponse(deleteFileRequest.deletedFileUrl());
    }
}