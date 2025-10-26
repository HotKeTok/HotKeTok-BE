package com.hotketok.dto;

import java.util.List;

public record DeleteImagesRequest(
        String profileImage,
        List<String> introductionImages
) {
}