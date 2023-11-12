package com.sparta.team2project.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OtherUsersProfileRequestDto {
    @NotBlank
    private String nickName;
}
