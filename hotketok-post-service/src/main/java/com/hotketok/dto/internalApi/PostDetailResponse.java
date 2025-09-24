package com.hotketok.dto.internalApi;

import com.hotketok.domain.Post;
//import com.hotketok.dto.internalApi.UserProfileResponse;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long senderId,
        //String senderNumber,
        LocalDateTime createdAt,
        String tag,
        String content
) {
    public static PostDetailResponse of (Post post) {
    //public static PostDetailResponse of(Post post, UserProfileResponse senderProfile) {
        //String senderNumber = (senderProfile != null) ? senderProfile.number() : "알 수 없음";

        String tagName = (post.getPostTag() != null) ? post.getPostTag().getContent() : null;

        return new PostDetailResponse(
                post.getSenderId(),
                //senderNumber,
                post.getCreatedAt(),
                tagName,
                post.getContent()
        );
    }
}
