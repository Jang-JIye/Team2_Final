package com.sparta.team2project.users;

import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.security.UserDetailsImpl;
import com.sparta.team2project.email.dto.EmailRequestDto;
import com.sparta.team2project.users.dto.CheckNickNameRequestDto;
import com.sparta.team2project.users.dto.LoginRequestDto;
import com.sparta.team2project.users.dto.SignoutRequestDto;
import com.sparta.team2project.users.dto.SignupRequestDto;

import com.sparta.team2project.email.dto.ValidNumberRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "사용자 관련 API", description = "사용자 관련 API")
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    // 회원가입(이메일 가입)
    @Operation(summary = "회원가입", description = "회원가입 api 입니다.")
    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signup(@RequestBody @Valid SignupRequestDto signupRequestDto) {
        return userService.signup(signupRequestDto);
    }

    // 이메일 요청 보내기
    @Operation(summary = "이메일 요청 보내기", description = "이메일 요청 보내는 api 입니다.")
    @PostMapping("/signup/email")
    public ResponseEntity<MessageResponseDto> sendEmail(@RequestBody @Valid EmailRequestDto emailRequestDto) {
        return userService.checkEmail(emailRequestDto.getEmail());
    }

    // 인증번호 확인하기
    @Operation(summary = "인증번호 확인", description = "인증번호 확인하는 api 입니다.")
    @PostMapping("/signup/email/valid")
    public ResponseEntity<Boolean> checkValidNumber(@RequestBody ValidNumberRequestDto validNumberRequestDto) {
        boolean checkNumber = userService.checkValidNumber(validNumberRequestDto, validNumberRequestDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(checkNumber);
    }

    //닉네임 중복확인
    @Operation(summary = "닉네임 중복확인", description = "닉네임 중복을 확인하는 api 입니다.")
    @PostMapping("/signup/check-nickname")
    public ResponseEntity<Boolean> checkNickName(@RequestBody CheckNickNameRequestDto checkNickNameRequestDto) {
        boolean checkNumber = userService.checkNickName(checkNickNameRequestDto.getNickName());
        return ResponseEntity.status(HttpStatus.OK).body(checkNumber);
    }



    // 회원 탈퇴
    @Operation(summary = "회원탈퇴", description = "회원탈퇴 api 입니다.")
    @DeleteMapping("/signout")
    public ResponseEntity<MessageResponseDto> deleteUser(@RequestBody SignoutRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.deleteUser(requestDto, userDetails.getEmail());
    }

    @Operation(summary = "로그인", description = "로그인 api 입니다.")
    @PostMapping("/login")
    public ResponseEntity<MessageResponseDto> login(@RequestBody LoginRequestDto requestDto,
                                                                 HttpServletResponse response) {
        return userService.login(requestDto,response);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 api 입니다.")
    @PostMapping("logout")
    public ResponseEntity<MessageResponseDto> logout(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse response) {
        return userService.logout(userDetails.getEmail(), response);
    }
}
