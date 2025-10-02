package com.hotketok.externalApi;

import com.hotketok.domain.Notice;
import com.hotketok.dto.CreateNoticeRequest;
import com.hotketok.dto.NoticeDetailResponse;
import com.hotketok.dto.NoticeResponse;
import com.hotketok.dto.UpdateNoticeRequest;
import com.hotketok.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notice-service")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 조회
    @GetMapping("/list")
    public List<NoticeResponse> getNoticeList(@RequestHeader("userId") Long userId) {
        return noticeService.getNoticeList(userId);
    }

    // 공지사항 세부 조회
    @GetMapping
    public NoticeDetailResponse getNoticeDetail(@RequestHeader("userId") Long userId, @RequestParam Long noticeId) {
        return noticeService.getNoticeDetail(userId, noticeId);
    }

    // 공지사항 작성
    @PostMapping
    public void createNotice(@RequestHeader("userId") Long userId, @RequestBody CreateNoticeRequest request) {
        noticeService.createNotice(userId, request);
    }

    // 공지사항 수정
    @PatchMapping
    public void updateNotice(@RequestHeader("userId") Long userId, @RequestBody UpdateNoticeRequest request) {
        noticeService.updateNotice(userId, request);
    }

    // 공지사항 삭제
    @DeleteMapping
    public void deleteNotice(@RequestHeader("userId") Long userId, @RequestParam Long noticeId) {
        noticeService.deleteNotice(userId, noticeId);
    }
}
