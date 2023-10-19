package com.sparta.team2project.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {

    @Email // 이메일 형식이어야 함
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,15}$", message = "비밀번호는 영어소문자, 숫자, 특수문자( !@#$%^&* )를 모두 포함한 8~15자의 문자열이어야 합니다.")
    private String password;

    @Pattern(regexp = "^[A-Za-z0-9가-힣]{2,10}$", message = "닉네임은 2글자 이상, 10글자 이하의 영문자, 숫자, 또는 한글로만 구성되어야 합니다.")
    private String nickName; // 추가된 부분

    private String profileImg; // 추가된 부분


    private boolean admin = false;
    private String adminToken = "";
}
