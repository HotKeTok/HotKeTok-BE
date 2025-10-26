package com.hotketok.exception;

import com.hotketok.hotketokcommonservice.error.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EstimateErrorCode implements ErrorCode {
    ESTIMATE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 견적서가 존재하지 않습니다."),
    PRICE_IS_ESSENTIAL(HttpStatus.BAD_REQUEST, "'나중에 결정'이 아닐 경우, 견적 가격은 필수입니다."),
    NO_AUTHORITY_TO_SELECT(HttpStatus.FORBIDDEN, "견적서를 선택할 권한이 없습니다."),
    NO_AUTHORITY_TO_DELETE(HttpStatus.FORBIDDEN, "견적서를 삭제할 권한이 없습니다."),
    ESTIMATE_NOT_MATCHING(HttpStatus.BAD_REQUEST, "견적서가 매칭된 상태가 아닙니다.");

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
