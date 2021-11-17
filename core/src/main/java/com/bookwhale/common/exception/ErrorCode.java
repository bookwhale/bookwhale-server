package com.bookwhale.common.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(500, "S_001", "서버에 문제가 생겼습니다."),
    FAILED_TO_SEND_MAIL(500, "S_002", "메일 전송에 실패했습니다."),
    INTERNAL_NAVER_SERVER_ERROR(500, "S_003", "네이버 책 API 서버에 문제가 생겼습니다."),
    FAILED_CONVERT_XML(500, "S_004", "XML 파일을 변환하는 과정에서 문제가 발생했습니다."),

    USER_ACCESS_DENIED(401, "AU_001", "해당 콘텐츠에 접근 권한이 없는 유저입니다."),
    UNAUTHORIZED_REDIRECT_URI(400, "AU_002", "인증되지 않은 REDIRECT_URI 입니다."),

    INVALID_INPUT_VALUE(400, "C_001", "적절하지 않은 요청 값입니다."),
    INVALID_TYPE_VALUE(400, "C_002", "요청 값의 타입이 잘못되었습니다."),
    NOT_EXIST_REQUESTPART(400, "C_003", "RequestPart는 필수 값입니다."),
    METHOD_NOT_ALLOWED(405, "C_004", "적절하지 않은 HTTP 메소드입니다."),

    DUPLICATED_USER_IDENTITY(400, "U_001", "이미 존재하는 아이디입니다."),
    INVALID_USER_PASSWORD(400, "U_002", "잘못된 비밀번호입니다."),

    INVALID_POST_ID(400, "P_001", "잘못된 게시글 ID 입니다."),
    NOT_EXIST_POST_ID(400, "P_002", "존재하지 않는 게시글 ID 입니다."),

    INVALID_LIKE_ID(400, "I_001", "잘못된 관심목록 ID 입니다."),
    DUPLICATED_LIKE(400, "I_002", "이미 관심목록에 등록된 게시글입니다."),

    INVALID_CHATROOM_ID(400, "CHAT_001", "잘못된 채팅방 ID 입니다."),
    INVALID_SELLER_ID(400, "CHAT_002", "잘못된 판매자 ID 입니다."),
    INVALID_POST_STATUS(400, "P_003", "판매중인 게시글만 판매자에게 채팅을 보낼 수 있습니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
