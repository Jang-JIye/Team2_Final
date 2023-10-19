package com.sparta.team2project.pictures.controller;

import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.exceptionhandler.CustomException;
import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
import com.sparta.team2project.commons.security.UserDetailsImpl;
import com.sparta.team2project.pictures.dto.PicturesMessageResponseDto;
import com.sparta.team2project.pictures.dto.PicturesResponseDto;
import com.sparta.team2project.pictures.dto.UploadResponseDto;
import com.sparta.team2project.pictures.service.PicturesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PicturesController {

    private final PicturesService picturesService;

    @PostMapping("/schedules/{schedulesId}/pictures")
    public UploadResponseDto uploadPictures(@PathVariable("schedulesId") Long schedulesId,
                                            @RequestPart("file") List<MultipartFile> files,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails
                                            ){
        // 업로드할 사진이 3개를 초과하면 예외 출력
        if(files.size() > 3){
            throw new CustomException(ErrorCode.EXCEED_PICTURES_LIMIT);
        }
        // 그 외의 경우 업로드 수행
        else{
            return picturesService.uploadPictures(schedulesId, files, userDetails.getUsers());
        }
    }

    @GetMapping("/schedules/{schedulesId}/pictures")
    public UploadResponseDto getPictures(@PathVariable("schedulesId") Long schedulesId){
        return picturesService.getPictures(schedulesId);
    }

    @GetMapping("/pictures/{picturesId}")
    public PicturesResponseDto getPicture(@PathVariable("picturesId") Long picturesId){
        return picturesService.getPicture(picturesId);
    }

    @PutMapping("/schedules/{schedulesId}/pictures")
    public PicturesMessageResponseDto updatePictures(@PathVariable("schedulesId") Long schedulesId,
                                                     @RequestParam("file") MultipartFile file,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails
                                          ){
        return picturesService.updatePictures(schedulesId, file, userDetails.getUsers());
    }


    @DeleteMapping("/pictures/{picturesId}")
    public MessageResponseDto deletePictures(@PathVariable("picturesId") Long picturesId,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails
                                                   ){
        return picturesService.deletePictures(picturesId, userDetails.getUsers());
    }

}