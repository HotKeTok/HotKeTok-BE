package com.hotketok.externalApi;

import com.hotketok.dto.internalApi.*;
import com.hotketok.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-service")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 받은 쪽지 목록 조회
    @GetMapping("/receive-list")
    public List<PostResponse> getReceiveList() {
        Long userId = 101L; // 임시데이터
        return postService.getReceiveList(userId);
    }

    // 보낸 쪽지 목록 조회
    @GetMapping("/send-list")
    public List<PostResponse> getSendList() {
        Long userId = 101L; // 임시데이터
        return postService.getSendList(userId);
    }

    // 쪽지 내용 상세 조회
    @GetMapping("/detail")
    public PostDetailResponse getPostDetail(@RequestParam Long postId) {
        Long userId = 101L;
        return postService.getPostDetail(postId, userId);
    }

    // 쪽지 쓰기
    @PostMapping("/write")
    public ResponseEntity<Void> sendPost(@RequestBody SendPostRequest request) {
        Long userId = 101L;
        postService.sendPost(userId, request);
        return ResponseEntity.accepted().build();
    }

    // 이웃 목록 조회
    @GetMapping("/tenant-list")
    public ResponseEntity<List<FloorResponse>> getAllHouseTags() {
        Long userId = 102L; // 임시 데이터
        List<FloorResponse> response = postService.getAllHouseTags(userId);
        return ResponseEntity.ok(response);
    }
}
