package com.hotketok.service;

import com.hotketok.domain.Post;
import com.hotketok.domain.PostTag;
import com.hotketok.dto.internalApi.PostDetailResponse;
import com.hotketok.dto.internalApi.PostResponse;
import com.hotketok.dto.internalApi.SendPostRequest;
import com.hotketok.repository.PostRepository;
import com.hotketok.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;

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

    // 쪽지 쓰기
    @Transactional
    public void sendPost(Long senderId, SendPostRequest request) {
        // 1. Post 엔티티 먼저 생성 (태그 연결 없이)
        Post post = Post.builder()
                .senderId(senderId)
                .receiverId(request.receiverId())
                .content(request.detailContent())
                .isAnonymous(request.isAnonymous())
                .isAnonymous(Boolean.valueOf(request.silentTime()))
                .build();

        List<String> tagNames = request.tags();

        // 2. 태그 존재하면 각 태그 처리
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                Optional<PostTag> tag = postTagRepository.findByContent(tagName);
                // 3. Post와 연관관계 사용해 중간 테이블로 연결
                post.addTag(tag);
            }
        }

        // 4. Post 저장 -> PostToTag도 같이 저장됨
        postRepository.save(post);
    }
}