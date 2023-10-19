package com.sparta.team2project.profile.controller;

import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.security.UserDetailsImpl;
import com.sparta.team2project.profile.dto.ProfileImgRequestDto;
import com.sparta.team2project.profile.service.ProfileService;
import com.sparta.team2project.profile.dto.PasswordRequestDto;
import com.sparta.team2project.profile.dto.ProfileNickNameRequestDto;
import com.sparta.team2project.profile.dto.ProfileResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "마이 프로필 관련 API", description = "마이 프로필 관련 API")
@RequestMapping("/api/users/profile")
public class ProfileController {

    private final ProfileService profileService;

    // 마이페이지 조회하기
    @Operation(summary = "마이 페이지 조회", description = "마이 페이지 조회 api 입니다.")
    @GetMapping()
    public ResponseEntity<ProfileResponseDto> getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.getProfile(userDetails.getUsers());
    }

    // 프로필 수정하기(닉네임)
    @Operation(summary = "마이 페이지 프로필 수정(닉네임)", description = "마이 페이지 (닉네임)프로필 수정 api 입니다.")
    @PutMapping("/update-nickName")
    public ResponseEntity<MessageResponseDto> updateProfile(@RequestBody ProfileNickNameRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.updateNickName(requestDto, userDetails.getUsers());
    }
    // 프로필 수정하기(프로필이미지)
    @Operation(summary = "마이 페이지 프로필 수정(프로필사진)", description = "마이 페이지 (프로필사진)프로필 수정 api 입니다.")
    @PutMapping("/update-profileImg")
    public ResponseEntity<MessageResponseDto> updateProfile(@RequestBody ProfileImgRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.updateProfileImg(requestDto, userDetails.getUsers());
    }

    //프로필 수정하기(비밀번호)
    @Operation(summary = "마이 페이지 프로필 수정(비밀번호)", description = "마이 페이지 (비밀번호)프로필 수정 api 입니다.")
    @PutMapping("/update-password")
    public ResponseEntity<MessageResponseDto> updatePassword(@RequestBody PasswordRequestDto requestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.updatePassword(requestDto, userDetails.getUsers());
    }

    // 프로필 수정하기(자기소개)
    @Operation(summary = "마이 페이지 프로필 수정(자기소개)", description = "마이 페이지 (자기소개)프로필 수정 api 입니다.")
    @PutMapping("/upadate-aboutMe")
    public ResponseEntity<MessageResponseDto> updateAboutMe(@RequestBody ProfileNickNameRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.updateAboutMe(requestDto, userDetails.getUsers());
    }
}