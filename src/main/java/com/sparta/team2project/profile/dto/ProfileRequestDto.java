package com.sparta.team2project.profile.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;


@Getter
public class ProfileRequestDto {

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9가-힣]{2,10}$", message = "닉네임은 2글자 이상, 10글자 이하의 영문자, 숫자, 또는 한글로만 구성되어야 합니다.")
    private String updateNickName; // 수정할 닉네임
    @NotBlank
    private String updateProfileImg; // 수정할 닉네임


}
