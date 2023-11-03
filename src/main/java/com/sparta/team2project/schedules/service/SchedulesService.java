package com.sparta.team2project.schedules.service;

import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.commons.exceptionhandler.CustomException;
import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
import com.sparta.team2project.schedules.dto.CreateSchedulesRequestDto;
import com.sparta.team2project.schedules.dto.SchedulesRequestDto;
import com.sparta.team2project.schedules.dto.SchedulesResponseDto;
import com.sparta.team2project.schedules.entity.Schedules;
import com.sparta.team2project.schedules.repository.SchedulesRepository;
import com.sparta.team2project.tripdate.entity.TripDate;
import com.sparta.team2project.tripdate.repository.TripDateRepository;
import com.sparta.team2project.users.UserRepository;
import com.sparta.team2project.users.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SchedulesService {

    // 사용할 Repository 선언
    private final SchedulesRepository schedulesRepository;
    private final UserRepository userRepository;
    private final TripDateRepository tripDateRepository;


   
    // schedules 생성 메서드
    public List<SchedulesResponseDto>  createSchedules(Long tripDateId, CreateSchedulesRequestDto requestDto, Users users) {
        TripDate tripDate = tripDateRepository.findById(tripDateId).
                orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        Users existUser = checkUser(users);

        checkAuthority(existUser,tripDate.getPosts().getUsers());

        List<Schedules> schedulesList = new ArrayList<>();
        List<SchedulesResponseDto> schedulesResponseDtoList=new ArrayList<>();
        for(SchedulesRequestDto schedulesRequestDto: requestDto.getSchedulesList()) {
            Schedules schedulesCreated = new Schedules(tripDate,schedulesRequestDto);
            // Repository에 저장하기 위한 리스트
            schedulesList.add(schedulesCreated);
            // DTO로 반환하기 위한 리스트
            SchedulesResponseDto schedulesResponseDto = new SchedulesResponseDto(schedulesCreated);
            schedulesResponseDtoList.add(schedulesResponseDto);
        }


        schedulesRepository.saveAll(schedulesList);
        return schedulesResponseDtoList;

    }


    // Schedules 조회 메서드 (권한 확인 없음)
    public SchedulesResponseDto getSchedules(Long schedulesId) {
        Schedules schedules = findSchedules(schedulesId);
        return new SchedulesResponseDto(schedules);
    }

    // 유저 유효 확인 메서드
    private Users checkUser (Users users) {
        return userRepository.findByEmail(users.getEmail()).
                orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_MATCH));
    }

    // 유저 권한 검사 메서드
    private void checkAuthority(Users existUser,Users users){
        if (!existUser.getUserRole().equals(UserRoleEnum.ADMIN)&&!existUser.getEmail().equals(users.getEmail())) {
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }
    }

    // schedules를 Repository에서 찾는 메서드
    private Schedules findSchedules(Long id) {
        return schedulesRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_MATCH));
    }


    // schedules 수정 메서드
    @Transactional
    public SchedulesResponseDto updateSchedules(Long schedulesId, Users users, SchedulesRequestDto requestDto) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users);         // 권한 확인
        Schedules schedules = findSchedules(schedulesId); // 해당 세부일정 찾기
        schedules.update(requestDto); //세부일정 업데이트
        return new SchedulesResponseDto(schedules); // ResponseDto에 실어서 반환
    }

    // schedules 삭제 메서드
    public MessageResponseDto deleteSchedules(Long schedulesId, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users);         // 권한 확인
        Schedules schedules = findSchedules(schedulesId);
        schedulesRepository.delete(schedules);
        MessageResponseDto messageResponseDto = new MessageResponseDto("삭제가 되었습니다.", 200);
        return messageResponseDto;
    }

}
