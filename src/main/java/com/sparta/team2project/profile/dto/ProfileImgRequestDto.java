package com.sparta.team2project.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProfileImgRequestDto {
    @NotBlank
    private String updateProfileImg; // 수정할 이미지
}
