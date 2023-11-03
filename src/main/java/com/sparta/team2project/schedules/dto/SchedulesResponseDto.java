package com.sparta.team2project.schedules.dto;

import com.sparta.team2project.pictures.dto.PicturesResponseDto;
import com.sparta.team2project.pictures.entity.Pictures;
import com.sparta.team2project.schedules.entity.Schedules;
import com.sparta.team2project.schedules.entity.SchedulesCategory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class SchedulesResponseDto {
    private Long schedulesId;
//    private LocalDate chosenDate; // 해당 스케줄에 대한 요일 반환
    private SchedulesCategory schedulesCategory;
//    private String details;
    private int costs;
    private String placeName;
    private String contents;
    private String timeSpent;
    //    private LocalTime startTime;
//    private LocalTime endTime;
    private String referenceURL;
    private String x;
    private String y;
    private List<PicturesResponseDto> picturesResponseDtoList;

    public SchedulesResponseDto(Schedules schedules){
        this.schedulesId = schedules.getId();
//        this.chosenDate = schedules.getTripDate().getChosenDate(); // 해당 스케줄에 대한 요일 반환
        this.schedulesCategory = schedules.getSchedulesCategory();
//        this.details = schedules.getDetails();
        this.costs = schedules.getCosts();
        this.placeName = schedules.getPlaceName();
        this.contents = schedules.getContents();
//        this.startTime = schedules.getStartTime();
//        this.endTime = schedules.getEndTime();
        this.timeSpent = schedules.getTimeSpent();
        this.referenceURL = schedules.getReferenceURL();
        this.x = schedules.getX();
        this.y = schedules.getY();
        this.picturesResponseDtoList = picturesToDto(schedules);
    }

    public List<PicturesResponseDto> picturesToDto(Schedules schedules){
        List<Pictures> picturesList = schedules.getPicturesList();
        List<PicturesResponseDto> picturesResponseDtoList = new ArrayList<>();
        for(Pictures pictures: picturesList){
            PicturesResponseDto picturesResponseDto = new PicturesResponseDto(pictures);
            picturesResponseDtoList.add(picturesResponseDto);
        }
        return picturesResponseDtoList;
    }
}
