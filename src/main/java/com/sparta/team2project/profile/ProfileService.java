package com.sparta.team2project.profile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.commons.exceptionhandler.CustomException;
import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
import com.sparta.team2project.profile.dto.*;
import com.sparta.team2project.s3.CustomMultipartFile;
import com.sparta.team2project.users.UserRepository;
import com.sparta.team2project.users.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 마이페이지 조회하기
    public ResponseEntity<ProfileResponseDto> getProfile(Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users); //권한 확인
        Profile findProfile = checkProfile(users); // 마이페이지 찾기

        ProfileResponseDto responseDto = new ProfileResponseDto(checkUser(users), checkProfile(users));

        return ResponseEntity.ok(responseDto);
    }

    // 마이페이지 수정하기(닉네임)
    @Transactional
    public ResponseEntity<MessageResponseDto> updateNickName(ProfileNickNameRequestDto requestDto, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users); //권한 확인
        Profile findProfile = checkProfile(users); // 마이페이지 찾기

        //닉네임 업데이트
        if (userRepository.existsByNickName(requestDto.getUpdateNickName())) {
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }
        findProfile.getUsers().updateNickName(requestDto);
        profileRepository.save(findProfile);

        MessageResponseDto responseDto = new MessageResponseDto("마이페이지 수정 성공", 200);
        return ResponseEntity.ok(responseDto);
    }

    // 마이페이지 수정하기(프로필이미지)
    @Transactional
    public String updateProfileImg(MultipartFile file, Users users) {
        // 1. 권한 확인
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users); //권한 확인
        Profile findProfile = checkProfile(users); // 프로필 확인
        // 2. 파일 정보 추출
        String picturesName = file.getOriginalFilename();
        String picturesURL = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com" + "/" + "profileImg" + "/" + picturesName;
        String pictureContentType = file.getContentType();
        // 3. 이미지 사이즈 재조정
        MultipartFile resizedImage = resizer(file, 96, 96);
        // 4. 사진을 메타데이터 및 정보와 함께 S3에 저장
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(resizedImage.getContentType());
        metadata.setContentLength(resizedImage.getSize());
        try (InputStream inputStream = resizedImage.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket + "/profileImg", picturesName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_NOT_UPLOAD);
        }
        //프로필이미지 업데이트
        findProfile.getUsers().updateProfileImg(picturesURL);
        profileRepository.save(findProfile);

        return picturesURL;
    }

    public String defaultProfileImg(Users users) {
        // 1. 권한 확인
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users); //권한 확인
        Profile findProfile = checkProfile(users); // 프로필 확인
        String defaultPictureURL = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fb0SLv8%2FbtsyLoUxvAs%2FSKsGiOc7TzkebNvH4ZQE9K%2Fimg.png";
        findProfile.getUsers().updateProfileImg(defaultPictureURL);
        profileRepository.save(findProfile);
        return defaultPictureURL;
    }
    public String readProfileImg(Long userId, Users users) {
        String profileURL = users.getProfileImg();
        return profileURL;
    }


    @Transactional
    public MultipartFile resizer(MultipartFile originalImage, int targetWidth, int targetHeight) {

        try {
            BufferedImage image = ImageIO.read(originalImage.getInputStream());
            int originWidth = image.getWidth();
            int originHeight = image.getHeight();
            System.out.println("originWidth: " + originWidth);
            System.out.println("originHeight: " + originHeight);
            // 이미지 품질 설정
// Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
// Image.SCALE_FAST : 이미지 부드러움보다 속도 우선
// Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
// Image.SCALE_SMOOTH : 속도보다 이미지 부드러움을 우선
// Image.SCALE_AREA_AVERAGING : 평균 알고리즘 사용
            Image processedImage = image.getScaledInstance(
                    targetWidth, targetHeight,
                    Image.SCALE_SMOOTH
            );
            BufferedImage newImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = newImage.getGraphics();
            graphics.drawImage(processedImage, 0, 0, null);
            graphics.dispose();
            String fileFormatName = originalImage.getContentType().substring(originalImage.getContentType().lastIndexOf("/") + 1);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(newImage, fileFormatName, baos);
            baos.flush();
            byte[] content = baos.toByteArray();

            return new CustomMultipartFile(
                    originalImage.getName(),
                    originalImage.getOriginalFilename(),
                    originalImage.getContentType(),
                    content
            );

        } catch (IOException e) {
            throw new CustomException(ErrorCode.UNABLE_TO_CONVERT);
        }
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

        MessageResponseDto responseDto = new MessageResponseDto("내 정보 수정 완료", 200);
        return ResponseEntity.ok(responseDto);
    }

    // 마이페이지 수정하기 (자기소개)
    public ResponseEntity<MessageResponseDto> updateAboutMe(AboutMeRequestDto requestDto, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users); //권한 확인
        Profile findProfile = checkProfile(users); // 마이페이지 찾기

        findProfile.updateAboutMe(requestDto);
        profileRepository.save(findProfile);

        MessageResponseDto responseDto = new MessageResponseDto("내 정보 수정 완료", 200);
        return ResponseEntity.ok(responseDto);
    }

    // 타사용자 마이페이지 조회하기
    public ResponseEntity<ProfileResponseDto> getOtherUsersProfile(OtherUsersProfileRequestDto requestDto, Users users) {
        Users otherUser = checkUser(requestDto.getNickName()); // 타 사용자 확인
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users); //권한 확인

        Profile otherUserProfile = profileRepository.findByUsers(otherUser)
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_NOT_EXIST));
        if (requestDto.getNickName() == null) {
            throw new CustomException(ErrorCode.NULL_NICKNAME);
        }

        ProfileResponseDto responseDto = new ProfileResponseDto(otherUser, otherUserProfile);

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

    private Users checkUser(String nickname) {
        return userRepository.findByNickName(nickname)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_NICKNAME));
    }

}
