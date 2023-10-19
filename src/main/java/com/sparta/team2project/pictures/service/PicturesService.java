package com.sparta.team2project.pictures.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.commons.exceptionhandler.CustomException;
import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
import com.sparta.team2project.pictures.dto.PicturesMessageResponseDto;
import com.sparta.team2project.pictures.dto.PicturesResponseDto;
import com.sparta.team2project.pictures.dto.UploadResponseDto;
import com.sparta.team2project.pictures.entity.Pictures;
import com.sparta.team2project.pictures.repository.PicturesRepository;
import com.sparta.team2project.s3.AmazonS3ResourceStorage;
import com.sparta.team2project.s3.FileDetail;
import com.sparta.team2project.schedules.entity.Schedules;
import com.sparta.team2project.schedules.repository.SchedulesRepository;
import com.sparta.team2project.users.UserRepository;
import com.sparta.team2project.users.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PicturesService {
    private final AmazonS3ResourceStorage amazonS3ResourceStorage;
    private final AmazonS3Client amazonS3Client;
    private final PicturesRepository picturesRepository;
    private final SchedulesRepository schedulesRepository;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public FileDetail save(MultipartFile multipartFile) {
        FileDetail fileDetail = FileDetail.multipartOf(multipartFile);
        amazonS3ResourceStorage.store(fileDetail.getPath(), multipartFile);
        return fileDetail;
    }

    // 사진 등록
    public UploadResponseDto uploadPictures(Long schedulesId, List<MultipartFile> files, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users);         // 권한 확인
        // 기 존재하는 사진이 있는지 확인
        Schedules checkSchedules = schedulesRepository.findById(schedulesId).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
        );
        List<Pictures> checkPicturesList = checkSchedules.getPicturesList();
        List<PicturesResponseDto> picturesResponseDtoList = new ArrayList<>(3);
        // 1. 파일 정보를 picturesResponseDtoList에 저장
        for (MultipartFile file : files) {
            String picturesName = file.getOriginalFilename();
            String picturesURL = "https://" + bucket + "/" + picturesName;
            String pictureContentType = file.getContentType();
            Long pictureSize = file.getSize();  // 단위: KBytes
            PicturesResponseDto picturesResponseDto = new PicturesResponseDto(
                    schedulesId, picturesURL, picturesName, pictureContentType, pictureSize);
            picturesResponseDtoList.add(picturesResponseDto);
            // 2. Repository에 파일 정보를 저장하기 위해 PicturesList에 저장(schedulesId 필요)
            Schedules schedules = schedulesRepository.findById(schedulesId).orElseThrow(
                    () -> new CustomException(ErrorCode.ID_NOT_MATCH)
            );
            Pictures pictures = new Pictures(schedules, picturesURL, picturesName, pictureContentType, pictureSize);
            checkPicturesList.add(pictures);
            // 3. 사진을 메타데이터 및 정보와 함께 S3에 저장
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            try {
                amazonS3Client.putObject(bucket, picturesName, file.getInputStream(), metadata);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 4. Repository에 Pictures리스트를 저장
        picturesRepository.saveAll(checkPicturesList);// 5. 성공 메시지 DTO와 함께 picturesResponseDtoList를 반환
        MessageResponseDto messageResponseDto = new MessageResponseDto("아래 파일들이 등록되었습니다.", 200);
        UploadResponseDto uploadResponseDto = new UploadResponseDto(picturesResponseDtoList, messageResponseDto);
        return uploadResponseDto;
    }


    public UploadResponseDto getPictures(Long schedulesId) {
        // 1. Schedules 객체를 찾아 연결된 Pictures 불러오기
        Schedules schedules = schedulesRepository.findById(schedulesId).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
        );
        // 2. 불러온 Pictures의 리스트를 DTO의 리스트로 변환
        List<Pictures> picturesList = schedules.getPicturesList();
        List<PicturesResponseDto> picturesResponseDtoList = new ArrayList<>(3);
        for (Pictures pictures : picturesList) {
            // 3. 파일 불러오기
            try {
                S3Object s3Object = amazonS3Client.getObject(bucket, pictures.getPicturesName());
                S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
                FileOutputStream fileOutputStream = new FileOutputStream(new File(pictures.getPicturesName()));
                byte[] read_buf = new byte[1024];
                int read_len = 0;
                while ((read_len = s3ObjectInputStream.read(read_buf)) > 0) {
                    fileOutputStream.write(read_buf, 0, read_len);
                }
                s3ObjectInputStream.close();
                fileOutputStream.close();
            } catch (AmazonServiceException e) {
                throw new AmazonServiceException(e.getErrorMessage());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            // 4. 각 사진 파일 정보(Pictures)를 DTO리스트에 저장
            PicturesResponseDto picturesResponseDto = new PicturesResponseDto(pictures);
            picturesResponseDtoList.add(picturesResponseDto);
        }
        // 5. 성공 메시지와 함께 사진 정보 반환
        MessageResponseDto messageResponseDto = new MessageResponseDto("요청한 파일을 반환하였습니다.", 200);
        UploadResponseDto uploadResponseDto = new UploadResponseDto(picturesResponseDtoList, messageResponseDto);
        return uploadResponseDto;
    }

    public PicturesResponseDto getPicture(Long picturesId) {
        try {
            // 1. 파일을 찾아 열기
            Pictures pictures = picturesRepository.findById(picturesId).orElseThrow(
                    () -> new CustomException(ErrorCode.ID_NOT_MATCH)
            );
            S3Object s3Object = amazonS3Client.getObject(bucket, pictures.getPicturesName());
            S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(pictures.getPicturesName()));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3ObjectInputStream.read(read_buf)) > 0) {
                fileOutputStream.write(read_buf, 0, read_len);
            }
            s3ObjectInputStream.close();
            fileOutputStream.close();
            // 2. 사진 파일 정보(Pictures) 반환
            return new PicturesResponseDto(pictures);
        } catch (AmazonServiceException e) {
            throw new AmazonServiceException(e.getErrorMessage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PicturesMessageResponseDto updatePictures(Long schedulesId, MultipartFile file, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users);         // 권한 확인
        Schedules schedules = schedulesRepository.findById(schedulesId).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_MATCH));
        List<Pictures> picturesList = schedules.getPicturesList();
        // 기 등록된 사진이 이미 3개 인지 검사해서 이미 3개 일 경우, 예외 메시지 출력
        if(picturesList.size() == 3){
            throw new CustomException(ErrorCode.EXCEED_PICTURES_LIMIT);
        }
        // 아닐 경우 요청된 사진 입력
        else{
            String picturesName = file.getOriginalFilename();
            String picturesURL = "https://" + bucket + "/" + picturesName;
            String pictureContentType = file.getContentType();
            Long pictureSize = file.getSize();  // 단위: KBytes
            PicturesResponseDto picturesResponseDto = new PicturesResponseDto(
                    schedulesId, picturesURL, picturesName, pictureContentType, pictureSize);
            Pictures pictures = new Pictures(schedules, picturesURL, picturesName, pictureContentType, pictureSize);
            picturesRepository.save(pictures);
            // 3. 사진을 메타데이터 및 정보와 함께 S3에 저장
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            try {
                amazonS3Client.putObject(bucket, picturesName, file.getInputStream(), metadata);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            MessageResponseDto messageResponseDto = new MessageResponseDto("사진이 업데이트 되었습니다.", 200);
            PicturesMessageResponseDto picturesMessageResponseDto = new PicturesMessageResponseDto(picturesResponseDto, messageResponseDto);
            return picturesMessageResponseDto;
        }
    }

    public MessageResponseDto deletePictures(Long picturesId, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users);         // 권한 확인
        Pictures pictures = picturesRepository.findById(picturesId).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
        );
        try {
            amazonS3Client.deleteObject(bucket, pictures.getPicturesName());
        } catch (AmazonServiceException e) {
            throw new AmazonServiceException(e.getErrorMessage());
        }
        picturesRepository.delete(pictures);
        MessageResponseDto messageResponseDto = new MessageResponseDto("사진이 삭제되었습니다.", 200);
        return messageResponseDto;
    }

    // 사용자 조회 메서드
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
}
