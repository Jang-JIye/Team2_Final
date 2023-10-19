package com.sparta.team2project.commons.exceptionhandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    ID_NOT_MATCH(HttpStatus.BAD_REQUEST, "작성자가 일치하지 않습니다"),
    ID_NOT_FOUND(HttpStatus.NOT_FOUND, "아이디를 찾을 수 없습니다."),
    IMG_NULL(HttpStatus.BAD_REQUEST,"이미지를 찾을 수 없습니다."),
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "선택한 북마크는 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 유효하지 않습니다."),
    DUPLICATED_ID(HttpStatus.BAD_REQUEST, "중복된 아이디입니다."),
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    EMAIL_FORMAT_WRONG(HttpStatus.BAD_REQUEST, "잘못된 형식의 이메일입니다."),     // 이메일, 비번 등에 공통으로 사용
    PASSWORD_FORMAT_WRONG(HttpStatus.BAD_REQUEST, "잘못된 형식의 패스워드입니다."),
    POST_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 게시글입니다."),
    COMMENTS_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 댓글입니다."),
    REPLIES_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 대댓글입니다."),
    PROFILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "존재하지 않는 마이페이지입니다."),
    NOT_ALLOWED(HttpStatus.BAD_REQUEST, "권한이 없습니다."), // 로그인 안한 상태에서, 혹은 권한이 없는 작성/수정/삭제 접근 시
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    PLAN_NOT_FOUND(HttpStatus.BAD_REQUEST, "없는 여행일정입니다."),
    S3_NOT_UPLOAD(HttpStatus.BAD_REQUEST,"S3에 업로드가 되지 않았습니다."),
    EXCEED_PICTURES_LIMIT(HttpStatus.BAD_REQUEST, "업로드할 수 있는 사진은 최대 3개 입니다."),
    FILE_SIZE_LIMIT(HttpStatus.BAD_REQUEST, "업로드할 수 있는 사진의 최대 용량은 총 10MB입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "잘못된 패스워드입니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "등록되지 않은 이메일입니다."),
    POST_NOT_SEARCH(HttpStatus.BAD_REQUEST, "검색결과가 없습니다."),
    COMMENTS_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 댓글은 존재하지 않습니다."),

    //ValidNumber(이메일 인증 관련)
    INVALID_VALID_TOKEN(HttpStatus.BAD_REQUEST, "이메일로 인증번호를 찾을 수 없습니다."),
    VALID_TIME_OVER(HttpStatus.BAD_REQUEST, "인증 시간이 초과되었습니다"),
    WRONG_NUMBER(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호는 대소문자, 숫자, 특수문자( !@#$%^&* )로만 구성된 8~15자의 문자열이어야 합니다."),
    EMAIL_NOT_VALIDATED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않은 이메일입니다."),
    EMAIL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "이메일을 인증해주세요."),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST,"닉네임은 2글자 이상, 10글자 이하의 영문자, 숫자, 또는 한글로만 구성되어야 합니다."),

    CURRENT_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST,"현재 비밀번호와 바꾸려는 비밀번호가 같습니다.");


    private final HttpStatus statusCode;
    private final String msg;

    ErrorCode(HttpStatus statusCode, String msg){
        this.statusCode = statusCode;
        this.msg = msg;
    }

}
