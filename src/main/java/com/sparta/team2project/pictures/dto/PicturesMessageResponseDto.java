package com.sparta.team2project.pictures.dto;

import com.sparta.team2project.commons.dto.MessageResponseDto;
import lombok.Getter;

@Getter
public class PicturesMessageResponseDto {
    private final MessageResponseDto messageResponseDto;
    private final PicturesResponseDto picturesResponseDto;

    public PicturesMessageResponseDto(PicturesResponseDto picturesResponseDto, MessageResponseDto messageResponseDto) {
        this.messageResponseDto = messageResponseDto;
        this.picturesResponseDto = picturesResponseDto;
    }
}
