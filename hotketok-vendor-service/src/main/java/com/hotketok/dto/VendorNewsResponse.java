package com.hotketok.dto;

import com.hotketok.domain.News;

import java.time.LocalDateTime;

public record VendorNewsResponse (
        Long newsId,
        String authorName,
        String authorProfileImage,
        String title,
        String content,
        LocalDateTime createdAt
) {
    public static VendorNewsResponse of(News news, VendorInfoAllResponse vendorInfo) {
        String authorName = (vendorInfo != null) ? vendorInfo.name() : "(알 수 없음)";
        String authorProfileImage = (vendorInfo != null) ? vendorInfo.image() : null;

        return new VendorNewsResponse(
                news.getId(),
                authorName,
                authorProfileImage,
                news.getTitle(),
                news.getContent(),
                news.getCreatedAt()
        );
    }
}
