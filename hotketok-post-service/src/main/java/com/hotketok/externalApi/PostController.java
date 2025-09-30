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
        Long userId = 101L;
        return postService.getReceiveList(userId);
    }

    // 보낸 쪽지 목록 조회
    @GetMapping("/send-list")
    public List<PostResponse> getSendList(@RequestHeader("userId") Long userId) {
        return postService.getSendList(userId);
    }

    // 쪽지 내용 상세 조회
    @GetMapping("/detail")
    public PostDetailResponse getPostDetail(@RequestHeader("userId") Long userId, @RequestParam Long postId) {
        return postService.getPostDetail(postId, userId);
    }

    // 쪽지 쓰기
    @PostMapping("/write")
    public void sendPost(@RequestHeader("userId") Long userId, @RequestBody SendPostRequest request) {
        postService.sendPost(userId, request);
    }

    // 이웃 목록 조회
    @GetMapping("/tenant-list")
    public List<FloorResponse> getAllHouseTags() {
        Long userId = 101L;
        return postService.getAllHouseTags(userId);
    }
}
