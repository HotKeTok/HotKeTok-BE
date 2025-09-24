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
        if (post.getTags() != null && !post.getTags().isEmpty()) {
            tagNames = post.getTags().stream()
                    .map(PostTag::getContent)
                    .collect(Collectors.toList());
        } else {
            // 태그가 없는 경우 빈 리스트를 반환
            tagNames = Collections.emptyList();
        }
        return new PostDetailResponse(
                post.getSenderId(),
                post.getCreatedAt(),
                tagNames, // 수정된 태그 목록 사용
                post.getContent()
        );
    }
}
