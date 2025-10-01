package com.hotketok.service;

import com.hotketok.domain.Notice;
import com.hotketok.dto.NoticeResponse;
import com.hotketok.dto.internalApi.CurrentAddressResponse;
import com.hotketok.dto.internalApi.UserProfileResponse;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final UserServiceClient userServiceClient;
    private final NoticeRepository noticeRepository;

    // 공지사항 목록 조회
    public List<NoticeResponse> getNoticeList(Long userId) {
        CurrentAddressResponse addressResponse = userServiceClient.getCurrentAddress(userId);
        String currentAddress = addressResponse.currentAddress();

        List<Notice> notices = noticeRepository.findByAddressOrderByIsFixDescCreatedAtDesc(currentAddress);

        return notices.stream()
                .map(notice -> {
                    Long authorId = notice.getAuthorId();
                    // 기존 메서드 활용을 위해 List 형식으로 요청 (채팅 서비스와 동일한 메서드 사용)
                    List<UserProfileResponse> profiles = userServiceClient.getUserProfilesByIds(List.of(authorId));
                    UserProfileResponse userProfileResponse = (!profiles.isEmpty()) ? profiles.get(0) : null;
                    return NoticeResponse.of(notice, userProfileResponse);
                })
                .collect(Collectors.toList());
    }
}
