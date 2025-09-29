package com.hotketok.dto.internalApi;

import com.hotketok.domain.Post;
import com.hotketok.domain.PostTag;
//import com.hotketok.dto.internalApi.UserProfileResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record PostDetailResponse(
        Long senderId,
        //String senderNumber,
        LocalDateTime createdAt,
        List<String> tags,
        String content
) {
    public static PostDetailResponse of(Post post) {
        List<String> tagNames;

        if (post.getPostToTags() != null && !post.getPostToTags().isEmpty()) {
            // PostToTag 객체 스트리밍해서 PostTag 얻고 최종 이름 가져옴
            tagNames = post.getPostToTags().stream()
                    .map(postToTag -> postToTag.getTag().getContent())
                    .collect(Collectors.toList());
        } else {
            tagNames = Collections.emptyList();
        }

        return new PostDetailResponse(
                post.getSenderId(),
                post.getCreatedAt(),
                tagNames,
                post.getContent()
        );
    }
}
