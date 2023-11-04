//package com.sparta.team2project.schedules;
//
//import com.sparta.team2project.commons.dto.MessageResponseDto;
//import com.sparta.team2project.commons.entity.UserRoleEnum;
//import com.sparta.team2project.commons.exceptionhandler.CustomException;
//import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
//import com.sparta.team2project.pictures.entity.Pictures;
//import com.sparta.team2project.posts.dto.TripDateOnlyRequestDto;
//import com.sparta.team2project.posts.entity.PostCategory;
//import com.sparta.team2project.posts.entity.Posts;
//import com.sparta.team2project.schedules.dto.CreateSchedulesRequestDto;
//import com.sparta.team2project.schedules.dto.SchedulesRequestDto;
//import com.sparta.team2project.schedules.dto.SchedulesResponseDto;
//import com.sparta.team2project.schedules.entity.Schedules;
//import com.sparta.team2project.schedules.repository.SchedulesRepository;
//import com.sparta.team2project.schedules.service.SchedulesService;
//import com.sparta.team2project.tripdate.entity.TripDate;
//import com.sparta.team2project.tripdate.repository.TripDateRepository;
//import com.sparta.team2project.users.UserRepository;
//import com.sparta.team2project.users.Users;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.sparta.team2project.schedules.entity.SchedulesCategory.카페;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class SchedulesServiceTest {
//
//    private SchedulesService schedulesService;
//    private SchedulesRepository schedulesRepository;
//    private UserRepository userRepository;
//    private TripDateRepository tripDateRepository;
//
//    @BeforeEach
//    public void setUp() {
//        schedulesRepository = Mockito.mock(SchedulesRepository.class);
//        userRepository = Mockito.mock(UserRepository.class);
//        tripDateRepository = Mockito.mock(TripDateRepository.class);
//        schedulesService = new SchedulesService(schedulesRepository, userRepository, tripDateRepository);
//    }
//
//    public SchedulesRequestDto MockSchedulesRequestDto(){
//        // Mock the MenuRequestDto
//        SchedulesRequestDto schedulesRequestDto = mock(SchedulesRequestDto.class);
//        // Set up the behavior of the mock DTO
//        when(schedulesRequestDto.getSchedulesCategory()).thenReturn(카페);
//        when(schedulesRequestDto.getCosts()).thenReturn(10000);
//        when(schedulesRequestDto.getPlaceName()).thenReturn("정동진 카페");
//        when(schedulesRequestDto.getContents()).thenReturn("해돋이 카페");
//        when(schedulesRequestDto.getTimeSpent()).thenReturn("3시간");
//        when(schedulesRequestDto.getReferenceURL()).thenReturn("www.blog.com");
//        return schedulesRequestDto;
//    }
//
//    public CreateSchedulesRequestDto MockCreateSchedulesRequestDto(){
//        CreateSchedulesRequestDto createSchedulesRequestDto = mock(CreateSchedulesRequestDto.class);
//        List<SchedulesRequestDto> schedulesRequestDtoList = new ArrayList<>();
//        schedulesRequestDtoList.add(MockSchedulesRequestDto());
//        when(createSchedulesRequestDto.getSchedulesList()).thenReturn(schedulesRequestDtoList);
//        return createSchedulesRequestDto;
//    }
//
//    public TripDateOnlyRequestDto MockTripDateOnlyRequestDto(){
//        TripDateOnlyRequestDto tripDateOnlyRequestDto = mock(TripDateOnlyRequestDto.class);
//        when(tripDateOnlyRequestDto.getChosenDate()).thenReturn(LocalDate.of(2023, 10, 10));
//        return tripDateOnlyRequestDto;
//    }
//
//
//
//    public Users MockUsers(){
//        return new Users("test@email.com", "test", "test123!", UserRoleEnum.USER, "image/profileImg.png");
//    }
//
//    public Posts MockPosts(){
//        return new Posts("해돋이 보러간다", "정동진 해돋이", PostCategory.가족, "동해안 해돋이", MockUsers());
//    }
//
//    public TripDate MockTripDate(){
//        return new TripDate(MockTripDateOnlyRequestDto(), MockPosts());
//    }
//
//    public Schedules MockSchedules(){
//        return new Schedules(MockTripDate(), MockSchedulesRequestDto());
//    }
//
//    public Pictures MockPictures(){
//        return new Pictures(MockSchedules(), "image/test.png", "test.png", "image/png", 100000L);
//    }
//
//
//
//    @Test
//    public void testCreateSchedules() {
//        // Set up your test data and expectations
//        Long tripDateId = 1L;
//        SchedulesRequestDto schedulesRequestDto = MockSchedulesRequestDto();
//        CreateSchedulesRequestDto createSchedulesRequestDto = MockCreateSchedulesRequestDto();
//        Users users = MockUsers();
//        TripDate tripDate = MockTripDate();
//        when(tripDateRepository.findById(tripDateId)).thenReturn(java.util.Optional.of(tripDate));
//        when(userRepository.findByEmail(users.getEmail())).thenReturn(java.util.Optional.of(users));
//
//        TripDate tripDateReturn = tripDateRepository.findById(tripDateId).orElseThrow(
//                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//        );
//
//        Users usersReturn = userRepository.findByEmail(users.getEmail()).orElseThrow(
//                () -> new CustomException(ErrorCode.EMAIL_NOT_FOUND)
//        );
//
//        // 입력값과 Repository에서 검색한 값 비교
//        System.out.println("Check chosenDate");
//        System.out.println("tripDate.getChosenDate(): " + tripDate.getChosenDate());
//        System.out.println("tripDateReturn.getChosenDate(): " + tripDateReturn.getChosenDate());
//        assertEquals(tripDate.getChosenDate(), tripDateReturn.getChosenDate());
//
//        System.out.println(" ".repeat(50));
//
//
//        System.out.println("Check email");
//        System.out.println("users.getEmail(): " + users.getEmail());
//        System.out.println("usersReturn.getEmail(): " + usersReturn.getEmail());
//        assertEquals(users.getEmail(), usersReturn.getEmail());
//
//        System.out.println(" ".repeat(50));
//
//
//        List<SchedulesResponseDto> schedulesResponseDtoList = schedulesService.createSchedules(tripDateId, createSchedulesRequestDto, users);
//
//        System.out.println("생성한 schedule의 아이디 값: " + schedulesResponseDtoList.get(0).getSchedulesId());
//
//        System.out.println(" ".repeat(50));
//
//        // Verify the result and expectations
//        System.out.println("schedulesRequestDto.getContents(): " + schedulesRequestDto.getContents());
//        System.out.println("schedulesResponseDtoList.get(0).getContents(): " + schedulesResponseDtoList.get(0).getContents());;
//        assertEquals(schedulesRequestDto.getContents(), schedulesResponseDtoList.get(0).getContents());
//
//
//    }
//
//    @Test
//    public void testCreateSchedulesWithInvalidTripDate() {
//        // Set up your test data and expectations
//        Long tripDateId = 1L;
//        CreateSchedulesRequestDto requestDto = MockCreateSchedulesRequestDto();
//        Users users = MockUsers();
//        when(tripDateRepository.findById(tripDateId)).thenReturn(java.util.Optional.empty());
//
//        // Call the method you want to test and expect an exception
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            schedulesService.createSchedules(tripDateId, requestDto, users);
//        });
//
//        // CustomException에서 정의한 에러 코드와 발생한 exception의 에러 코드 비교
//        System.out.println("ErrorCode.PLAN_NOT_FOUND: " + ErrorCode.PLAN_NOT_FOUND);
//        System.out.println("exception.getErrorCode(): " + exception.getErrorCode());
//
//        assertEquals(ErrorCode.PLAN_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    public void testGetSchedules() {
//        // Set up your test data and expectations
//        Long schedulesId = 1L;
//        Schedules schedules = MockSchedules();
//        when(schedulesRepository.findById(schedulesId)).thenReturn(java.util.Optional.of(schedules));
//
//        // Call the method you want to test
//        SchedulesResponseDto response = schedulesService.getSchedules(schedulesId);
//
//        // Verify the result and expectations
//        assertNotNull(response);
//        System.out.println("schedules.getSchedulesCategory(): " + schedules.getSchedulesCategory());
//        System.out.println("response.getSchedulesCategory(): " + response.getSchedulesCategory());
//        assertEquals(schedules.getSchedulesCategory(), response.getSchedulesCategory());
//    }
//
//    // Write similar test methods for updateSchedules and deleteSchedules
//
//    @Test
//    public void testUpdateSchedules() {
//        // Set up your test data and expectations
//        Long schedulesId = 1L;
//        Users users = MockUsers();
//        SchedulesRequestDto requestDto = MockSchedulesRequestDto();
//        Schedules schedules = MockSchedules();
//        when(schedulesRepository.findById(schedulesId)).thenReturn(java.util.Optional.of(schedules));
//        when(userRepository.findByEmail(users.getEmail())).thenReturn(java.util.Optional.of(users));
//
//        // Call the method you want to test
//        SchedulesResponseDto response = schedulesService.updateSchedules(schedulesId, users, requestDto);
//
//        // Verify the result and expectations
//        System.out.println("schedules.getSchedulesCategory(): " + schedules.getSchedulesCategory());
//        System.out.println("response.getSchedulesCategory(): " + response.getSchedulesCategory());
//        assertNotNull(response);
//        assertEquals(schedules.getSchedulesCategory(), response.getSchedulesCategory());
//    }
//
//    @Test
//    public void testDeleteSchedules() {
//        // Set up your test data and expectations
//        Long schedulesId = 1L;
//        Users users = MockUsers();
//        Schedules schedules = MockSchedules();
//        when(schedulesRepository.findById(schedulesId)).thenReturn(java.util.Optional.of(schedules));
//        when(userRepository.findByEmail(users.getEmail())).thenReturn(java.util.Optional.of(users));
//
//        // Call the method you want to test
//        MessageResponseDto response = schedulesService.deleteSchedules(schedulesId, users);
//
//        Schedules schedulesReturn = schedulesRepository.findById(schedulesId).orElseThrow(
//                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//        );
//
//        // 주어진 ID값과 일치하지 않는지 확인
//        assertNotEquals(schedulesId, schedulesReturn.getId());
//
//        // Verify the result and expectations
//        assertNotNull(response);
//        System.out.println("response.getMsg(): " + response.getMsg());
//        assertEquals("삭제가 되었습니다.", response.getMsg());
//        assertEquals(HttpServletResponse.SC_OK, response.getStatusCode());
//    }
//}
