package com.fc.mini3server._core.handler;

public interface Message {
    String INVALID_ID_PARAMETER = "해당 아이디가 존재하지 않습니다.";
    String INVALID_USER_STATUS_NOT_APPROVED = "미승인 상태의 계정만 승인 가능합니다.";
    String INVALID_USER_STATUS_APPROVED = "재직 중인 계정만 변경 가능합니다.";
    String METHOD_ARGUMENT_TYPE_MISMATCH = "유효하지 않은 파라미터입니다.";

    String INVALID_REGISTER_FORMAT = "요청 형식이 잘못 되었습니다. 올바른 직급, 병원, 또는 부서 번호를 입력 하였는지 확인하십시오.";
    String INVALID_USER_NOT_APPROVED = "인증되지 않은 사용자 입니다.";
    String INVALID_NOT_VALID_TOKEN = "올바른 토큰이 아닙니다.";
    String INVALID_NO_TOKEN_MATCHED_WITH_USER = "토큰 정보와 일치하는 회원이 없습니다.";
    String INVALID_PASSWORD = "입력하신 비밀번호가 일치하지 않습니다.";
    String HOSPITAL_NOT_FOUND = "존재하지 않는 병원입니다.";
    String DEPT_NOT_FOUND = "존재하지 않는 부서입니다.";
    String EXCEED_MAX_FILE_SIZE = "업로드한 이미지의 크기가 1MB를 초과합니다.";
    String IO_EXCEPTION_WHEN_FILE_UPLOADING = "이미지 파일 저장 중 문제가 발생했습니다.";
}
