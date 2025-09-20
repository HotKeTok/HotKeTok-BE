package com.hotketok.exception;

import com.hotketok.hotketokcommonservice.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ImageErrorCode implements ErrorCode {
    DELETE_FAILED(HttpStatus.BAD_REQUEST, "이미지 삭제에 실패했습니다."),
    UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "이미지 업로드에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorName() {
        return this.message;
    }
}
