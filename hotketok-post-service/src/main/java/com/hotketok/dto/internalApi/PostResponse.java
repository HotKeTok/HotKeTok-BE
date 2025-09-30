package com.hotketok.dto.internalApi;

import com.hotketok.domain.Post;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        Long senderId,
        String number, // 보낸 사람 호수
        String content,
        LocalDateTime createdAt,
        Boolean anonymous
) {
    public static PostResponse of(Post post, HouseInfoResponse tenantInfo) {
        String number = null;
        if (tenantInfo != null && tenantInfo.floor() != null && tenantInfo.number() != null) {
//            number = tenantInfo.floor() + " " + tenantInfo.number();
            number = tenantInfo.number();
        }

        return new PostResponse(
                post.getId(),
                post.getSenderId(),
                number,
                post.getContent(),
                post.getCreatedAt(),
                post.getIsAnonymous()
        );
    }
}
