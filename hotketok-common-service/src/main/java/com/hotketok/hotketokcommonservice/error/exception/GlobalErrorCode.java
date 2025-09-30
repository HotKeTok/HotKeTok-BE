package com.hotketok.hotketokcommonservice.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    BAD_REQUEST(HttpStatus.BAD_REQUEST,  "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,  "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,  "금지된 요청입니다."),
    INVALID_REQUEST_INFO(HttpStatus.BAD_REQUEST,  "요청된 정보가 올바르지 않습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "유효성 검증에 실패했습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST,  "유효하지 않은 파라미터입니다."),
    METHOD_ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST,"Enum Type이 일치하지 않아 Binding에 실패하였습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,"지원하지 않는 HTTP method 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,  "서버 에러, 관리자에게 문의 바랍니다."),
    HOUSE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 집 정보를 찾을 수 없습니다.");
    ;

    private final HttpStatus status;
    private final String message;
}
