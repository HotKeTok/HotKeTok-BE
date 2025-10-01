package com.hotketok.exception;

import com.hotketok.hotketokcommonservice.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum NoticeErrorCode implements ErrorCode {
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 공지사항을 찾을 수 없습니다."),
    NO_AUTHORITY_TO_UPDATE(HttpStatus.FORBIDDEN, "공지사항을 수정할 권한이 없습니다."),
    NO_AUTHORITY_TO_DELETE(HttpStatus.FORBIDDEN, "공지사항을 삭제할 권한이 없습니다.");
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorName() {
        return this.name();
    }
}

