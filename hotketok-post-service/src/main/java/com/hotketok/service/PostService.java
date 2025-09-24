package com.hotketok.service;

import com.hotketok.dto.internalApi.PostReceiveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    // 받은 쪽지 목록 조회
    public List<PostReceiveResponse> getReceiveList(Long userId) {

    }
}

