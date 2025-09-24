package com.hotketok.externalApi;

import com.hotketok.dto.internalApi.PostReceiveResponse;
import com.hotketok.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/post-service")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 받은 쪽지 목록 조회
    @GetMapping("/receive-list")
    public List<PostReceiveResponse> getReceiveList() {
        Long userId = 101L; // 임시데이터
        return postService.getReceiveList(userId);
    }
}
