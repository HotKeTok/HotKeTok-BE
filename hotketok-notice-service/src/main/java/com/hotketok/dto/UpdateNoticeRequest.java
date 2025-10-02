package com.hotketok.dto;

public record UpdateNoticeRequest(
        Long noticeId,
        String title,
        String content,
        Boolean isFix // boolean 대신 Boolean을 사용해 null 값을 허용함
) {
}