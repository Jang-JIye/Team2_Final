package com.sparta.team2project.pictures.dto;

import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.pictures.entity.Pictures;
import lombok.Getter;

import java.util.List;

@Getter
public class UploadResponseDto {

    private final MessageResponseDto messageResponseDto;
    private final List<PicturesResponseDto> picturesResponseDtoList;

    public UploadResponseDto(List<PicturesResponseDto> picturesResponseDtoList, MessageResponseDto messageResponseDto) {
        this.picturesResponseDtoList = picturesResponseDtoList;
        this.messageResponseDto = messageResponseDto;
    }
}
