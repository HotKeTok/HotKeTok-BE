package com.hotketok.service;

import com.hotketok.domain.Notice;
import com.hotketok.dto.CreateNoticeRequest;
import com.hotketok.dto.NoticeDetailResponse;
import com.hotketok.dto.NoticeResponse;
import com.hotketok.dto.UpdateNoticeRequest;
import com.hotketok.dto.internalApi.CurrentAddressResponse;
import com.hotketok.dto.internalApi.UserProfileResponse;
import com.hotketok.exception.NoticeErrorCode;
import com.hotketok.hotketokcommonservice.error.exception.CustomException;
import com.hotketok.internalApi.UserServiceClient;
import com.hotketok.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
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
                    UserProfileResponse userProfileResponse = (!profiles.isEmpty()) ? profiles.get(0) : null; // 없으면 빈 리스트 반환
                    return NoticeResponse.of(notice, userProfileResponse);
                })
                .collect(Collectors.toList());
    }

    // 공지사항 세부 조회
    public NoticeDetailResponse getNoticeDetail(Long userId, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(NoticeErrorCode.NOTICE_NOT_FOUND));

        Long authorId = notice.getAuthorId();

        List<UserProfileResponse> profiles = userServiceClient.getUserProfilesByIds(List.of(authorId));
        UserProfileResponse authorProfile = (!profiles.isEmpty()) ? profiles.get(0) : null;

        return NoticeDetailResponse.of(notice, authorProfile);
    }

    // 공지사항 작성
    @Transactional
    public void createNotice(Long userId, CreateNoticeRequest request) {
        CurrentAddressResponse addressResponse = userServiceClient.getCurrentAddress(userId);
        String currentAddress = addressResponse.currentAddress();

        Notice notice = Notice.createNotice(
                currentAddress,
                userId,
                request.title(),
                request.content(),
                request.isFix(),
                LocalDateTime.now()
        );

        noticeRepository.save(notice);
    }

    // 공지사항 수정
    @Transactional
    public void updateNotice(Long userId, UpdateNoticeRequest request) {
        Long noticeId = request.noticeId();
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(NoticeErrorCode.NOTICE_NOT_FOUND));

        if (!notice.getAuthorId().equals(userId)) {
            throw new CustomException(NoticeErrorCode.NO_AUTHORITY_TO_UPDATE);
        }

        notice.updateNotice(request.title(), request.content(), request.isFix());
    }

    // 공지사항 삭제
    @Transactional
    public void deleteNotice(Long userId, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(NoticeErrorCode.NOTICE_NOT_FOUND));

        if (!notice.getAuthorId().equals(userId)) {
            throw new CustomException(NoticeErrorCode.NO_AUTHORITY_TO_DELETE);
        }

        noticeRepository.delete(notice);
    }
}
