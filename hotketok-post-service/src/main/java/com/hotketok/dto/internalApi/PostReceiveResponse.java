package com.hotketok.dto.internalApi;

import com.hotketok.domain.Post;

import java.time.LocalDateTime;

public record PostReceiveResponse(
        Long postId,
        Long senderId,
        //String number, // 보낸 사람 호수
        String content,
        LocalDateTime createdAt,
        Boolean anonymous
) {
    public PostReceiveResponse(Post post) {
        this(
                post.getId(),
                post.getSenderId(),
                post.getContent(),
                post.getCreatedAt(),
                post.getIsAnonymous()
        );
    }
}
