package com.hotketok.dto;

import com.hotketok.domain.Notice;
import com.hotketok.dto.internalApi.UserProfileResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeResponse {
    private final String author;
    private final String authorProfileImage;
    private final String title;
    private final LocalDateTime date;
    private final boolean isFix;

    public static NoticeResponse of(Notice notice, UserProfileResponse authorInfo) {
        return NoticeResponse.builder()
                .author(authorInfo.userName())
                .authorProfileImage(authorInfo.profileImageUrl())
                .title(notice.getTitle())
                .date(notice.getCreatedAt())
                .isFix(notice.getIsFix())
                .build();
    }
}
