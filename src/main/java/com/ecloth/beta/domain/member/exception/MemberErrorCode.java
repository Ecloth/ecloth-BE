package com.ecloth.beta.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {

    // 400 BAD_REQUEST
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 가입된 이메일 입니다."),
    ALREADY_EXIST_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용중인 닉네임 입니다."),
    WRONG_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호 입니다."),
    INVALID_EMAIL_AUTH_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 인증 코드입니다."),
    UNVERIFIED_MEMBER(HttpStatus.BAD_REQUEST,"이메일 인증을 하지 않은 회원입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    INACTIVE_OR_SUSPENDED_MEMBER(HttpStatus.BAD_REQUEST, "탈퇴 또는 이용 정지 회원입니다."),
    NOT_FOUND_TOKEN(HttpStatus.BAD_REQUEST, "Authorization Header에 토큰이 없습니다."),
    // 401 UNAUTHORIZED
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.토큰 갱신 또는 다시 로그인해주세요."),

    // 403 FORBIDDEN
    ALREADY_LOGOUT_TOKEN(HttpStatus.FORBIDDEN, "이미 로그아웃 된 토큰입니다. 다시 로그인 해주세요."),

    // 404 NOT_FOUND
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,  "가입된 회원이 없습니다."),

    // 500 INTERNAL_SERVER_ERROR
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알수없는 오류");

    private final HttpStatus httpStatus;
    private final String detail;
}
