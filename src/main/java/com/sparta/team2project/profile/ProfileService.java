package com.sparta.team2project.profile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.commons.exceptionhandler.CustomException;
import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
import com.sparta.team2project.pictures.repository.PicturesRepository;
import com.sparta.team2project.profile.dto.*;
import com.sparta.team2project.s3.CustomMultipartFile;
import com.sparta.team2project.users.UserRepository;
import com.sparta.team2project.users.Users;
import lombok.RequiredArgsConstructor;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
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
    private final PicturesRepository picturesRepository;

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
        String fileFormatName = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
        // 3. 이미지 사이즈 재조정
//        MultipartFile resizedImage = resizer(picturesName, fileFormatName, file, 50);
        Long pictureSize = file.getSize();  // 단위: KBytes
        // 4. 사진을 메타데이터 및 정보와 함께 S3에 저장
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(pictureContentType);
        metadata.setContentLength(pictureSize);
        try (InputStream inputStream = file.getInputStream()) {
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
    public MultipartFile resizer(String fileName, String fileFormat, MultipartFile originalImage, int width) {

        try {
            BufferedImage image = ImageIO.read(originalImage.getInputStream());// MultipartFile -> BufferedImage Convert

            int originWidth = image.getWidth();
            int originHeight = image.getHeight();

            // origin 이미지가 400보다 작으면 패스
            if(originWidth < width)
                return originalImage;

            MarvinImage imageMarvin = new MarvinImage(image);

            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", width);
            scale.setAttribute("newHeight", width);//정사각형(너비 = 높이)으로 출력
            scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

            BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageNoAlpha, fileFormat, baos);
            baos.flush();

            return new CustomMultipartFile(fileName,fileFormat,originalImage.getContentType(), baos.toByteArray());

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
