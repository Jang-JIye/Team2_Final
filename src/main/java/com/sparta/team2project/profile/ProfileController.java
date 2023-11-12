package com.sparta.team2project.profile;

import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.security.UserDetailsImpl;
import com.sparta.team2project.profile.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "마이 프로필 관련 API", description = "마이 프로필 관련 API")
@RequestMapping("/api/users/profile")
public class ProfileController {

    private final ProfileService profileService;

    // 마이페이지 조회하기
    @Operation(summary = "마이 페이지 조회", description = "마이 페이지 조회 api 입니다.")
    @GetMapping()
    public ResponseEntity<ProfileResponseDto> getProfile (@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.getProfile(userDetails.getUsers());
    }

    // 프로필 수정하기(닉네임)
    @Operation(summary = "마이 페이지 프로필 수정(닉네임)", description = "마이 페이지 (닉네임)프로필 수정 api 입니다.")
    @PutMapping("/update-nickname")
    public ResponseEntity<MessageResponseDto> updateNickName(@RequestBody ProfileNickNameRequestDto requestDto,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.updateNickName(requestDto, userDetails.getUsers());
    }
    // 프로필 수정하기(프로필이미지)
    @Operation(summary = "마이 페이지 프로필 수정(프로필사진)", description = "마이 페이지 (프로필사진)프로필 수정 api 입니다.")
    @PutMapping("/update-profileImg")
    public String updateProfileImg(@RequestParam("file")MultipartFile file,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(file.getContentType() == null){
            String defaultProfileImgURL = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fb0SLv8%2FbtsyLoUxvAs%2FSKsGiOc7TzkebNvH4ZQE9K%2Fimg.png";
            return defaultProfileImgURL;
        }
        else{
            return profileService.updateProfileImg(file, userDetails.getUsers());
        }
    }

    // 프로필 수정하기(기본 이미지)
    @Operation(summary = "마이 페이지 프로필 수정(프로필사진, 기본이미지)", description = "마이 페이지 (프로필사진, 기본이미지)프로필 수정 api 입니다.")
    @PutMapping("/default-profileImg")
    public String defaultProfileImg(
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
            return profileService.defaultProfileImg(userDetails.getUsers());
    }

    // 프로필 사진 조회
    @Operation(summary = "마이 페이지 프로필 사진 조회", description = "마이 페이지 프로필 사진 조회 api 입니다.")
    @GetMapping("/users-profileImg/{userId}")
    public String readProfileImg(@PathVariable("userId") Long userId,
                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.readProfileImg(userId, userDetails.getUsers());
    }

    //프로필 수정하기(비밀번호)
    @Operation(summary = "마이 페이지 프로필 수정(비밀번호)", description = "마이 페이지 (비밀번호)프로필 수정 api 입니다.")
    @PutMapping("/update-password")
    public ResponseEntity<MessageResponseDto> updatePassword(@RequestBody PasswordRequestDto requestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.updatePassword(requestDto, userDetails.getUsers());
    }
    @Operation(summary = "마이 페이지 프로필 자기소개", description = "마이 페이지 자기소개 수정 api 입니다.")
    @PutMapping("/update-aboutMe")
    public ResponseEntity<MessageResponseDto> updateAboutMe(@Valid @RequestBody AboutMeRequestDto requestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.updateAboutMe(requestDto, userDetails.getUsers());
    }

    // 다른 사용자 마이페이지 조회하기(닉네임 별로...)
    @Operation(summary = "타사용자 마이페이지 조회하기", description = "타 사용자 마이페이지 조회 api 입니다.")
    @GetMapping("/nickName")
    public ResponseEntity<ProfileResponseDto> getOtherUsersProfile(@RequestBody OtherUsersProfileRequestDto requestDto,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return profileService.getOtherUsersProfile(requestDto, userDetails.getUsers());
    }
}
