package com.hotketok.dto;

import com.hotketok.domain.Notice;
import com.hotketok.dto.internalApi.UserProfileResponse;

import java.time.LocalDateTime;

public record NoticeResponse(
        String author,
        String authorProfileImage,
        String title,
        LocalDateTime date,
        boolean isFix
) {
    public static NoticeResponse of(Notice notice, UserProfileResponse authorProfile) {
        String authorName = (authorProfile != null) ? authorProfile.userName() : "(알 수 없음)";
        String profileImage = (authorProfile != null) ? authorProfile.profileImageUrl() : null;

        return new NoticeResponse(
                authorName,
                profileImage,
                notice.getTitle(),
                notice.getCreatedAt(),
                notice.getIsFix()
        );
    }
}