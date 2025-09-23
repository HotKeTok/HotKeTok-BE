package com.hotketok.service;

import com.hotketok.dto.internalApi.UserProfileResponse;
import com.hotketok.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    // 채팅 서비스에서 호출하는 여러 ID를 받아 사용자를 목록을 조회하는 메서드
    public List<UserProfileResponse> findUserProfilesByIds(List<Long> userIds) {
        return userRepository.findAllByIdIn(userIds).stream()
                .map(UserProfileResponse::from)
                .collect(Collectors.toList());
    }
}