package com.sparta.team2project.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PasswordRequestDto {

    // 현재비밀번호, 수정할 비밀번호
    @NotBlank
    private String currentPassword;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]{8,15}$", message = "비밀번호는 대소문자, 숫자, 특수문자( !@#$%^&* )로만 구성된 8~15자의 문자열이어야 합니다.")
    private String updatePassword;
}
