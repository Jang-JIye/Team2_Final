package com.sparta.team2project.users.social.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenDto {
    private String accessToken;
//    private String refreshToken;

    public TokenDto(String accessToken) {
        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
    }
}