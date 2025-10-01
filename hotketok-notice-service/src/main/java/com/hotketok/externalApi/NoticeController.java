package com.hotketok.externalApi;

import com.hotketok.domain.Notice;
import com.hotketok.dto.NoticeDetailResponse;
import com.hotketok.dto.NoticeResponse;
import com.hotketok.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notice-service")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 조회
    @GetMapping("/list")
    public List<NoticeResponse> getNoticeList() {
        Long userId = 101L;
        return noticeService.getNoticeList(userId);
    }

    // 공지사항 세부 조회
    @GetMapping(" ")
    public NoticeDetailResponse getNoticeDetail(@RequestParam Long noticeId) {
        Long userId = 101L;
        return noticeService.getNoticeDetail(userId, noticeId);
    }
}
