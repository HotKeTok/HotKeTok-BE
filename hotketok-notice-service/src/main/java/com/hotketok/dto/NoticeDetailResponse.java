package com.hotketok.dto;

import com.hotketok.domain.Notice;
import com.hotketok.dto.internalApi.UserProfileResponse;

import java.time.LocalDateTime;

public record NoticeDetailResponse(
        String title,
        String content,
        String author,
        String authorProfileImage,
        LocalDateTime createdAt
) {
    public static NoticeDetailResponse of(Notice notice, UserProfileResponse authorProfile) {
        String authorName = (authorProfile != null) ? authorProfile.userName() : "(알 수 없음)";
        String profileImage = (authorProfile != null) ? authorProfile.profileImageUrl() : null;

        return new NoticeDetailResponse(
                notice.getTitle(),
                notice.getContent(),
                authorName,
                profileImage,
                notice.getCreatedAt()
        );
    }
}