package com.hotketok.service;

import com.hotketok.domain.Post;
import com.hotketok.dto.internalApi.PostDetailResponse;
import com.hotketok.dto.internalApi.PostResponse;
import com.hotketok.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    // 받은 쪽지 목록 조회
    public List<PostResponse> getReceiveList(Long userId) {
        List<Post> posts = postRepository.findByReceiverId(userId);

        return posts.stream()
                .map(PostResponse::new) // 생성자 참조 사용해 간결하게 변환
                .collect(Collectors.toList());
    }

    // 보낸 쪽지 목록 조회
    public List<PostResponse> getSendList(Long userId) {
        List<Post> posts = postRepository.findBySenderId(userId);

        return posts.stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }

    // 쪽지 내용 상세 조회
    @Transactional
    public PostDetailResponse getPostDetail(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 쪽지가 존재하지 않습니다. ID: " + postId));

        // 유저의 쪽지인지 확인
        if (!post.getSenderId().equals(userId) && !post.getReceiverId().equals(userId)) {
            throw new IllegalArgumentException("해당 쪽지를 조회할 권한이 없습니다.");
        }

        return PostDetailResponse.of(post);
    }
}