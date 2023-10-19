package com.sparta.team2project.schedules.controller;


import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.security.UserDetailsImpl;
import com.sparta.team2project.schedules.dto.CreateSchedulesRequestDto;
import com.sparta.team2project.schedules.dto.SchedulesRequestDto;
import com.sparta.team2project.schedules.dto.SchedulesResponseDto;
import com.sparta.team2project.schedules.service.SchedulesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "세부 여행 일정 관련 API", description = "세부 여행 일정 관련 API")
@RequestMapping("/api")
public class SchedulesController {
    private final SchedulesService schedulesService;



    // 세부일정 생성
    @Operation(summary = "여행 일정 생성", description = "여행 일정 생성 api 입니다.")
    @PostMapping("/tripDate/{tripDateId}/schedules")
    public MessageResponseDto createSchedules(@PathVariable("tripDateId") Long tripDateId,
                                                              @RequestBody CreateSchedulesRequestDto requestDtoList,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        return schedulesService.createSchedules(tripDateId,requestDtoList,userDetails.getUsers());
    }
  
    // 세부일정 조회
    @Operation(summary = "여행 일정 조희", description = "여행 일정 조회 api 입니다.")
    @GetMapping("/schedules/{schedulesId}")
    public SchedulesResponseDto getSchedules(@PathVariable("schedulesId") Long schedulesId
    ) {
        return schedulesService.getSchedules(schedulesId);
    }

    // 세부일정 수정
    @Operation(summary = "여행 일정 수정", description = "여행 일정 수정 api 입니다.")
    @PutMapping("/schedules/{schedulesId}")
    public SchedulesResponseDto updateSchedules(@PathVariable("schedulesId") Long schedulesId,
                                                @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestBody SchedulesRequestDto schedulesRequestDto
    ) {
        return schedulesService.updateSchedules(schedulesId, userDetails.getUsers(), schedulesRequestDto);
    }

    // 세부일정 삭제
    @Operation(summary = "여행 일정 삭제", description = "여행 일정 삭제 api 입니다.")
    @DeleteMapping("/schedules/{schedulesId}")
    public MessageResponseDto deleteSchedules(@PathVariable("schedulesId") Long schedulesId,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return schedulesService.deleteSchedules(schedulesId, userDetails.getUsers());
    }

}
