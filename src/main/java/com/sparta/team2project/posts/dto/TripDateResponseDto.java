package com.sparta.team2project.posts.dto;

import com.sparta.team2project.tripdate.entity.TripDate;
import com.sparta.team2project.schedules.dto.SchedulesResponseDto;
import com.sparta.team2project.schedules.entity.Schedules;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class TripDateResponseDto {
    private final Long tripDateId;
    private final LocalDate chosenDate;
    private final String subTitle;
    private final List<SchedulesResponseDto> schedulesList;

    public TripDateResponseDto(TripDate tripDate){
        this.tripDateId = tripDate.getId();
        this.chosenDate = tripDate.getChosenDate();
        this.subTitle = tripDate.getSubTitle();
        this.schedulesList = schedulesToDto(tripDate.getSchedulesList());
    }

    public List<SchedulesResponseDto> schedulesToDto(List<Schedules> schedulesListBeforeDto){
        return schedulesListBeforeDto.stream().map(SchedulesResponseDto::new).collect(Collectors.toList());
    }
}
