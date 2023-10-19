package com.sparta.team2project.schedules.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateSchedulesRequestDto {
    private List<SchedulesRequestDto> schedulesList;
}

