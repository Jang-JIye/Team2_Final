//package com.sparta.team2project.profile;
//
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.sparta.team2project.commons.dto.MessageResponseDto;
//import com.sparta.team2project.commons.entity.UserRoleEnum;
//import com.sparta.team2project.commons.exceptionhandler.CustomException;
//import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
//import com.sparta.team2project.pictures.repository.PicturesRepository;
//import com.sparta.team2project.profile.dto.AboutMeRequestDto;
//import com.sparta.team2project.profile.dto.PasswordRequestDto;
//import com.sparta.team2project.profile.dto.ProfileNickNameRequestDto;
//import com.sparta.team2project.users.UserRepository;
//import com.sparta.team2project.users.UserService;
//import com.sparta.team2project.users.Users;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.net.MalformedURLException;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(SpringExtension.class)
//class ProfileServiceTest {
//    @InjectMocks
//    private ProfileService profileService;
//    @Mock
//    private ProfileRepository profileRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//
//
//    private Users createUserWithProfileImg(String profileImg) {
//        Users user = new Users("test@example.com", "TestUser", "password", UserRoleEnum.USER, profileImg);
//        return user;
//    }
//
//    @Nested
//    @DisplayName("GetProfile")
//    class GetProfile {
//        @Test
//        @DisplayName("프로필 조회 성공")
//        void getProfileSuccess() {
//            // 가짜 User 생성
//            Users mockUser = new Users("test@example.com", "TestUser", "password123", UserRoleEnum.USER, "profile.jpg");
//            // 가짜 Profile 생성
//            Profile mockProfile = new Profile(mockUser);
//
//            // UserRepository 가짜 User 반환하도록 설정, ProfileRepository  가짜 Profile 반환하도록 설정
//            when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
//            when(profileRepository.findById(mockProfile.getId())).thenReturn(Optional.of(mockProfile));
//
//            // UserRepository에서 User 불러오기, ProfileRepository에서 Profile 불러오기
//            Users findUser = userRepository.findById(mockUser.getId()).orElse(null);
//            Profile findProfile = profileRepository.findById(mockProfile.getId()).orElse(null);
//
//            // 관계 확인
//            assertEquals(findProfile.getUsers(), mockUser);
//        }
//
//        @Test
//        @DisplayName("프로필 이미지 URL을 읽어오기")
//        void readProfileImg() {
//            // given
//            String profileImg = "https://example.com/profile-image.jpg";
//            Users user = createUserWithProfileImg(profileImg);
//
//            // when
//            String profileURL = profileService.readProfileImg(user.getId(), user);
//
//            // then
//            assertEquals(profileImg, profileURL);
//        }
//
//        @Test
//        @DisplayName("프로필 조회 실패 - User 가 없는 경우")
//        void getProfileFailUserNotFound() {
//            // given
//            Users users = createUserWithProfileImg("profile.jpg");
//            String userEmail = users.getEmail();
//            // Mock 객체로 userRepository 설정
//            when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());
//
//            // when
//            Throwable exception = assertThrows(CustomException.class, () -> profileService.getProfile(users));
//
//            // then
//            assertEquals(ErrorCode.ID_NOT_MATCH, ((CustomException) exception).getErrorCode());
//        }
//        @Test
//        @DisplayName("프로필 조회 실패 - 프로필이 존재하지 않는 경우")
//        void getProfileFailCheckProfile() {
//            // given
//            Users users = createUserWithProfileImg("profile.jpg");
//
//            // 예외처리 없이 진행되는 경우 설정
//            when(userRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(users));
//            when(profileRepository.findByUsers_Email(users.getEmail())).thenReturn(Optional.empty());
//
//            // when
//            Throwable exception = assertThrows(CustomException.class, () -> profileService.getProfile(users));
//            // then
//            assertEquals(ErrorCode.PROFILE_NOT_EXIST, ((CustomException) exception).getErrorCode());
//        }
//    }
//
//
//    @Nested
//    @DisplayName("UpdateProfile")
//    class UpdateProfile {
//        @Test
//        @DisplayName("프로필 수정 성공 - 닉네임")
//        void updateNickNameSuccess() {
//            // Given
//            Users user = createUserWithProfileImg("profile.jpg");
//            Profile profile = new Profile(user);
//            ProfileNickNameRequestDto requestDto = new ProfileNickNameRequestDto();
//
//            // 예외처리 없이 진행되는 경우 설정
//            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//            when(profileRepository.findByUsers_Email(user.getEmail())).thenReturn(Optional.of(profile));
//
//            when(userRepository.existsByNickName(requestDto.getUpdateNickName())).thenReturn(false);
//
//            // When
//            ResponseEntity<MessageResponseDto> response = profileService.updateNickName(requestDto, user);
//
//            // Then
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertEquals("마이페이지 수정 성공", response.getBody().getMsg());
//            verify(userRepository, times(1)).existsByNickName(requestDto.getUpdateNickName());
//        }
//
//        @Test
//        @DisplayName("프로필 수정 실패 - 중복 닉네임")
//        void updateNickNameFailDuplicate() {
//            // Given
//            Users user = createUserWithProfileImg("profile.jpg");
//            Profile profile = new Profile(user);
//            ProfileNickNameRequestDto requestDto = new ProfileNickNameRequestDto();
//
//            // 예외처리 없이 진행되는 경우 설정
//            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//            when(profileRepository.findByUsers_Email(user.getEmail())).thenReturn(Optional.of(profile));
//            when(userRepository.existsByNickName(requestDto.getUpdateNickName())).thenReturn(true);
//            Throwable exception = assertThrows(CustomException.class, () -> profileService.updateNickName(requestDto, user));
//
//            // When
//            assertThrows(CustomException.class, () -> profileService.updateNickName(requestDto, user));
//            assertEquals(ErrorCode.DUPLICATED_NICKNAME, ((CustomException) exception).getErrorCode());
//        }
//
//        @Test
//        @DisplayName("프로필 수정성공 - 비밀번호")
//        void updatePasswordSuccess() {
//            // Given
//            Users user = createUserWithProfileImg("profile.jpg");
//            Profile profile = new Profile(user);
//            PasswordRequestDto requestDto = new PasswordRequestDto();
//
//            // 예외처리 없이 진행되는 경우 설정
//            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//            when(profileRepository.findByUsers_Email(user.getEmail())).thenReturn(Optional.of(profile));
//            when(passwordEncoder.matches(eq(requestDto.getCurrentPassword()), anyString())).thenReturn(true);
//
//            // 리플렉션을 사용하여 private 필드인 updatePassword에 값을 주입
//            try {
//                Field updatePasswordField = PasswordRequestDto.class.getDeclaredField("updatePassword");
//                updatePasswordField.setAccessible(true);
//                updatePasswordField.set(requestDto, "newPassword");
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//
//            // When
//            ResponseEntity<MessageResponseDto> response = profileService.updatePassword(requestDto, user);
//
//            // Then
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertEquals("내 정보 수정 완료", response.getBody().getMsg());
//        }
//
//        @Test
//        @DisplayName("프로필 수정실패 - 현재 비밀번호 확인 불일치")
//        void updatePasswordFailPasswordNotMatch() {
//            // Given
//            Users user = createUserWithProfileImg("profile.jpg");
//            Profile profile = new Profile(user);
//            PasswordRequestDto requestDto = new PasswordRequestDto();
//
//            // 예외처리 없이 진행되는 경우 설정
//            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//            when(profileRepository.findByUsers_Email(user.getEmail())).thenReturn(Optional.of(profile));
//            // 비밀번호가 일치하지 않을 경우 설정
//            when(passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())).thenReturn(false);
//
//            // When
//            Throwable exception = assertThrows(CustomException.class, () -> profileService.updatePassword(requestDto, user));
//
//            // Then
//            assertEquals(ErrorCode.CURRENT_PASSWORD_NOT_MATCH, ((CustomException) exception).getErrorCode());
//        }
//
//        @Test
//        @DisplayName("프로필 수정실패 - 동일한 비밀번호")
//        void updatePasswordFailSamePassword() throws Exception {
//            // Given
//            Users user = createUserWithProfileImg("profile.jpg");
//            Profile profile = new Profile(user);
//            PasswordRequestDto requestDto = new PasswordRequestDto();
//
//            // 리플렉션을 사용하여 PasswordRequestDto의 private 필드인 currentPassword, updatePassword에 값을 주입
//            Field currentPasswordField = PasswordRequestDto.class.getDeclaredField("currentPassword");
//            currentPasswordField.setAccessible(true);
//            currentPasswordField.set(requestDto, "samePassword");
//
//            Field updatePasswordField = PasswordRequestDto.class.getDeclaredField("updatePassword");
//            updatePasswordField.setAccessible(true);
//            updatePasswordField.set(requestDto, "samePassword");
//
//            // 예외처리 없이 진행되는 경우 설정
//            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//            when(profileRepository.findByUsers_Email(user.getEmail())).thenReturn(Optional.of(profile));
//            when(passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())).thenReturn(true);
//
//            // When
//            Throwable exception = assertThrows(CustomException.class, () -> profileService.updatePassword(requestDto, user));
//
//            // Then
//            assertEquals(ErrorCode.SAME_PASSWORD, ((CustomException) exception).getErrorCode());
//        }
//
//
//        @Test
//        @DisplayName("프로필 수정성공 - 자기소개")
//        void updateAboutMeSuccess() {
//            // Given
//            Users user = createUserWithProfileImg("profile.jpg");
//            Profile profile = new Profile(user);
//            AboutMeRequestDto requestDto = new AboutMeRequestDto();
//
//            // 예외처리 없이 진행되는 경우 설정
//            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//            when(profileRepository.findByUsers_Email(user.getEmail())).thenReturn(Optional.of(profile));
//
//            ResponseEntity<MessageResponseDto> response = profileService.updateAboutMe(requestDto, user);
//
//            // Then
//            assertEquals(HttpStatus.OK, response.getStatusCode());
//            assertEquals("내 정보 수정 완료", response.getBody().getMsg());
//        }
//    }
//}