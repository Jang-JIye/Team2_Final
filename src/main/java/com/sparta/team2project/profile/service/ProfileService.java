package com.sparta.team2project.profile.service;

import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.commons.exceptionhandler.CustomException;
import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
import com.sparta.team2project.profile.dto.PasswordRequestDto;
import com.sparta.team2project.profile.dto.ProfileRequestDto;
import com.sparta.team2project.profile.dto.ProfileResponseDto;
import com.sparta.team2project.profile.entity.Profile;
import com.sparta.team2project.profile.repository.ProfileRepository;
import com.sparta.team2project.users.UserRepository;
import com.sparta.team2project.users.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    // 마이페이지 조회하기
    public ResponseEntity<ProfileResponseDto> getProfile(Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users); //권한 확인

        ProfileResponseDto responseDto = new ProfileResponseDto(checkUser(users));

        return ResponseEntity.ok(responseDto);
    }

    // 마이페이지 수정하기(닉네임, 프로필이미지)
    @Transactional
    public ResponseEntity<MessageResponseDto> updateProfile(ProfileRequestDto requestDto, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users); //권한 확인
        Profile findProfile = checkProfile(users); // 마이페이지 찾기


        //닉네임, 프로필이미지 업데이트
        findProfile.getUsers().updateProfile(requestDto);
        profileRepository.save(findProfile);

        MessageResponseDto responseDto = new MessageResponseDto("마이페이지 수정 성공", 200);
        return ResponseEntity.ok(responseDto);
    }

    // 비밀번호 수정하기
    @Transactional
    public ResponseEntity<MessageResponseDto> updatePassword(PasswordRequestDto requestDto, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users); //권한 확인
        Profile findProfile = checkProfile(users); // 마이페이지 찾기

        // 현재 비밀번호 확인
        String currentPassword = requestDto.getCurrentPassword();
        if (!passwordEncoder.matches(currentPassword, findProfile.getUsers().getPassword())) {
            throw new CustomException(ErrorCode.CURRENT_PASSWORD_NOT_MATCH);
        }
        // 수정할 비밀번호가 현재 비밀번호와 같은 경우
        if (requestDto.getUpdatePassword().equals(requestDto.getCurrentPassword())) {
            throw new CustomException(ErrorCode.SAME_PASSWORD);
        }

        // 새로운 비밀번호 업데이트
        String updatePassword = requestDto.getUpdatePassword();

        // 새로운 비밀번호 인코딩 후 저장
        findProfile.getUsers().updatePassword(requestDto, passwordEncoder);
        profileRepository.save(findProfile);

        MessageResponseDto responseDto = new MessageResponseDto("내정보 수정 완료", 200);
        return ResponseEntity.ok(responseDto);
    }


    // 사용자 확인 메서드
    private Users checkUser(Users users) {
        return userRepository.findByEmail(users.getEmail()).
                orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_MATCH));
    }

    // ADMIN 권한 및 이메일 일치여부 메서드
    private void checkAuthority(Users existUser, Users users) {
        if (!existUser.getUserRole().equals(UserRoleEnum.ADMIN) && !existUser.getEmail().equals(users.getEmail())) {
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }
    }

    // 마이페이지 찾기
    private Profile checkProfile(Users users) {
        return profileRepository.findByUsers_Email(users.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_EXIST));

    }
}
