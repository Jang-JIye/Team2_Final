package com.sparta.team2project.posts.dto;

import com.sparta.team2project.schedules.dto.SchedulesResponseDto;
import com.sparta.team2project.schedules.entity.Schedules;
import com.sparta.team2project.tripdate.entity.TripDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class TripDateOnlyResponseDto {
    private final Long tripDateId;
    private final LocalDate chosenDate;
    private final String subTitle;

    public TripDateOnlyResponseDto(TripDate tripDate){
        this.tripDateId = tripDate.getId();
        this.chosenDate = tripDate.getChosenDate();
        this.subTitle = tripDate.getSubTitle();
    }
    
}