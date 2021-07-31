package com.teamherb.bookstoreback.common.exception.dto;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(500, "S_001", "서버에 문제가 생겼습니다."),

    USER_ACCESS_DENIED(401, "AU_001", "해당 콘텐츠에 접근 권한이 없는 유저입니다."),

    INVALID_INPUT_VALUE(400, "C_001", "적절하지 않은 요청 값입니다."),
    INVALID_TYPE_VALUE(400, "C_002", "요청 값의 타입이 잘못되었습니다."),
    METHOD_NOT_ALLOWED(405, "C_003", "적절하지 않은 HTTP 메소드입니다."),

    DUPLICATED_USER_IDENTITY(400, "US_001", "이미 존재하는 아이디입니다.");


    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
