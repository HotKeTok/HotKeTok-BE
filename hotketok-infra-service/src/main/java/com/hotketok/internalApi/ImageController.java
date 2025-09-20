package com.hotketok.internalApi;


import com.hotketok.dto.DeleteImageRequest;
import com.hotketok.dto.DeleteImageResponse;
import com.hotketok.dto.UploadImageResponse;
import com.hotketok.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/internal/infra-service")
public class ImageController {

    private final ImageStorageService imageStorageService;

    @Autowired
    public ImageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("/upload/imageList")
    public UploadImageResponse uploadImages(@RequestParam("files") List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String publicUrl = imageStorageService.uploadImage(file);
            urls.add(publicUrl);
        }
        return new UploadImageResponse(urls);
    }

    @DeleteMapping("/delete")
    public DeleteImageResponse deleteFile(@RequestBody DeleteImageRequest deleteImageRequest) throws IOException {
        imageStorageService.deleteImage(deleteImageRequest.deletedImageUrl());
        return new DeleteImageResponse(deleteImageRequest.deletedImageUrl());
    }
}