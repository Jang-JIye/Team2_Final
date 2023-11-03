//package com.sparta.team2project.tripdate;
//
//import com.sparta.team2project.commons.dto.MessageResponseDto;
//import com.sparta.team2project.commons.entity.UserRoleEnum;
//import com.sparta.team2project.commons.exceptionhandler.CustomException;
//import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
//import com.sparta.team2project.posts.dto.TripDateResponseDto;
//import com.sparta.team2project.posts.entity.PostCategory;
//import com.sparta.team2project.posts.entity.Posts;
//import com.sparta.team2project.schedules.service.SchedulesService;
//import com.sparta.team2project.tripdate.entity.TripDate;
//import com.sparta.team2project.tripdate.repository.TripDateRepository;
//import com.sparta.team2project.posts.dto.TripDateOnlyRequestDto;
//import com.sparta.team2project.posts.repository.PostsRepository;
//import com.sparta.team2project.schedules.repository.SchedulesRepository;
//import com.sparta.team2project.tripdate.service.TripDateService;
//import com.sparta.team2project.users.UserRepository;
//import com.sparta.team2project.users.Users;
//import jakarta.servlet.http.HttpServletResponse;
//import org.aspectj.lang.annotation.Before;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.mockito.InjectMocks;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//
//public class TripDateServiceTest {
//
//    private TripDateService tripDateService;
//
//    private TripDateRepository tripDateRepository;
//
//    private UserRepository userRepository;
//
//    private PostsRepository postsRepository;
//
//    private SchedulesRepository schedulesRepository;
//
//    @BeforeEach
//    public void init() {
//        schedulesRepository = Mockito.mock(SchedulesRepository.class);
//        userRepository = Mockito.mock(UserRepository.class);
//        tripDateRepository = Mockito.mock(TripDateRepository.class);
//        postsRepository = Mockito.mock(PostsRepository.class);
//        tripDateService = new TripDateService(tripDateRepository, userRepository, postsRepository, schedulesRepository);
//    }
//
//    public TripDateOnlyRequestDto MockTripDateOnlyRequestDto(){
//        TripDateOnlyRequestDto tripDateOnlyRequestDto = mock(TripDateOnlyRequestDto.class);
//        when(tripDateOnlyRequestDto.getChosenDate()).thenReturn(LocalDate.of(2023, 10, 10));
//        return tripDateOnlyRequestDto;
//    }
//
//    public TripDate MockTripDate(){
//        return new TripDate(MockTripDateOnlyRequestDto(), MockPosts());
//    }
//
//    public Posts MockPosts(){
//        return new Posts("해돋이 보러간다", "정동진 해돋이", PostCategory.가족, "동해안 해돋이", MockUsers());
//    }
//
//    public Users MockUsers(){
//        return new Users("test@email.com", "test", "test123!", UserRoleEnum.USER, "image/profileImg.png");
//    }
//    @Test
//    public void testGetTripDate() {
//        // Create a TripDate object for testing
//        TripDate testTripDate = MockTripDate();
//
//        // Mock the behavior of the tripDateRepository.findById method
//        when(tripDateRepository.findById(anyLong())).thenReturn(java.util.Optional.of(testTripDate));
//
//        TripDateResponseDto responseDto = tripDateService.getTripDate(1L);
//
//        // Verify that the service method returns the expected DTO
//        assertEquals(responseDto.getChosenDate(), testTripDate.getChosenDate());
//        // You can add more assertions based on your DTO structure
//        System.out.println("Check chosenDate");
//        System.out.println("responseDto.getChosenDate(): " + responseDto.getChosenDate());
//        System.out.println("testTripDate.getChosenDate(): " + testTripDate.getChosenDate());
//    }
//
//
//
//    @Test
//    public void testGetTripDateAll() {
//        // Mock the behavior of the postsRepository.findById method
//        when(postsRepository.findById(anyLong())).thenReturn(java.util.Optional.of(MockPosts()));
//
//        List<TripDate> tripDateList = new ArrayList<>();
//        TripDate tripDate = MockTripDate();
//        tripDateList.add(tripDate);
//        // Mock the behavior of the tripDateRepository.findByPosts method
//        when(tripDateRepository.findByPosts(any())).thenReturn(tripDateList);
//
//        List<TripDateResponseDto> responseDtoList = tripDateService.getTripDateAll(1L);
//
//        // Add assertions based on your DTO structure and mock data
//        System.out.println("Check chosenDate");
//        System.out.println("responseDtoList.get(0).getChosenDate(): " + responseDtoList.get(0).getChosenDate());
//        System.out.println("tripDateList.get(0).getChosenDate()" + tripDateList.get(0).getChosenDate());
//    }
//
//    @Test
//    public void testUpdateTripDate() {
//        // Create a test Users object and a TripDateOnlyRequestDto
//        Users testUser = MockUsers();
//        TripDateOnlyRequestDto requestDto = MockTripDateOnlyRequestDto();
//
//        // Mock the behavior of the userRepository.findByEmail method
//        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(testUser));
//
//        // Mock the behavior of the tripDateRepository.findById and update methods
//        TripDate testTripDate = MockTripDate();
//        when(tripDateRepository.findById(anyLong())).thenReturn(java.util.Optional.of(testTripDate));
//
//        // Call the updateTripDate method and assert the response
//        TripDateResponseDto responseDto = tripDateService.updateTripDate(1L, testUser, requestDto);
//
//        // Add assertions based on your DTO structure and expected behavior
//        // You can add more assertions based on your DTO structure
//        System.out.println("Check chosenDate");
//        System.out.println("responseDto.getChosenDate(): " + responseDto.getChosenDate());
//        System.out.println("testTripDate.getChosenDate(): " + testTripDate.getChosenDate());
//        assertEquals(testTripDate.getChosenDate(), responseDto.getChosenDate());
//    }
//
//    @Test
//    public void testDeleteTripDate() {
//        // Create a test Users object
//        Users testUser = MockUsers();
//
//        // Mock the behavior of the userRepository.findByEmail method
//        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(testUser));
//
//        // Mock the behavior of the tripDateRepository.findById and delete methods
//        TripDate testTripDate = MockTripDate();
//        when(tripDateRepository.findById(anyLong())).thenReturn(java.util.Optional.of(testTripDate));
//
//        MessageResponseDto messageResponseDto = tripDateService.deleteTripDate(1L, testUser);
//
//        TripDate tripDateReturn = tripDateRepository.findById(1L).orElseThrow(
//                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//        );
//        // Add assertions based on the expected behavior
//        assertNull(tripDateReturn.getId());
//
//        // Verify the result and expectations
//        assertNotNull(messageResponseDto);
//        System.out.println("messageResponseDto.getMsg(): " + messageResponseDto.getMsg());
//        assertEquals("삭제가 되었습니다.", messageResponseDto.getMsg());
//        assertEquals(HttpServletResponse.SC_OK, messageResponseDto.getStatusCode());
//    }
//}
