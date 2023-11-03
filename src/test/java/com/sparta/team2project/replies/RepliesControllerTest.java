//package com.sparta.team2project.replies;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.team2project.comments.controller.CommentsController;
//import com.sparta.team2project.comments.dto.CommentsRequestDto;
//import com.sparta.team2project.comments.dto.CommentsResponseDto;
//import com.sparta.team2project.comments.entity.Comments;
//import com.sparta.team2project.commons.config.WebSecurityConfig;
//import com.sparta.team2project.commons.dto.MessageResponseDto;
//import com.sparta.team2project.commons.security.UserDetailsImpl;
//import com.sparta.team2project.replies.controller.RepliesController;
//import com.sparta.team2project.replies.dto.RepliesRequestDto;
//import com.sparta.team2project.replies.dto.RepliesResponseDto;
//import com.sparta.team2project.replies.entity.Replies;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.security.Principal;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@WebMvcTest (
//        controllers = {RepliesController.class},
//        // 제외 할 것 지정
//        excludeFilters = {
//                @ComponentScan.Filter(
//                        type = FilterType.ASSIGNABLE_TYPE,
//                        classes = WebSecurityConfig.class
//                )
//        }
//)
//
//class RepliesControllerTest {
//
//    // 필요한 의존 객체의 타입에 해당하는 빈을 찾아 주입
//    @Autowired
//    private MockMvc mvc;
//
//    private Principal principal;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    RepliesController repliesController;
//
//    // 테스트할 USER 객체
//    private void mockUserSetup() {
//
//        // Mock 테스트 유저 생성
//        UserDetails userDetails = User.withUsername("rkawk@gmail.com")
//                .password("kim881012!!!")
//                .roles("USER") // 롤에 대한 설정
//                .build();
//        principal = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("대댓글 생성")
//    public void repliesCreateTest() throws Exception {
//        mockUserSetup();
//
//        // Given
//        Long commentId = 1L;
//        String contents = "대댓글";
//        RepliesRequestDto requestDto = new RepliesRequestDto(contents);
//
//        // eq : 특정한 값을 기대하는 경우에 사용됨
//        // any : 어떤 값이든 허용하는 경우에 사용됨
//        when(repliesController.repliesCreate(eq(commentId), any(RepliesRequestDto.class), any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new MessageResponseDto("대댓글을 작성하였습니다", 200), HttpStatus.OK));
//
//        // Wen and Then
//        mvc.perform(MockMvcRequestBuilders.post("/api/comments/" + commentId + "/replies")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto))
//                        .with(csrf()) // CSRF 토큰을 요청에 포함
//                        .principal(principal)) // 가짜 사용자 principal 설정
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
////    @WithMockUser
////    @Test
////    @DisplayName("대댓글 조회")
////    public void repliesListTest() throws Exception {
////
////        // Given
////        Long commentId = 1L;
////        Replies replies1 = new Replies();
////        Replies replies2 = new Replies();
////
////        List<RepliesResponseDto> responseDto = Arrays.asList(
////                new RepliesResponseDto(replies1),
////                new RepliesResponseDto(replies2)
////        );
////
////        // eq : 특정한 값을 기대하는 경우에 사용됨
////        // any : 어떤 값이든 허용하는 경우에 사용됨
////        when(repliesController.repliesList(eq(commentId), any(Pageable.class)))
////                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
////
////        // Wen and Then
////        mvc.perform(MockMvcRequestBuilders.get("/api/comments/"+commentId+"/replies")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(objectMapper.writeValueAsString(responseDto)))
////                        .andExpect(status().isOk())
////                        .andDo(print());
////    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("마이페이지에서 내가 쓴 댓글 조회")
//    public void repliesMeListTest() throws Exception {
//        mockUserSetup();
//
//        // Given
//        Replies replies1 = new Replies();
//        Replies replies2 = new Replies();
//
//        List<RepliesResponseDto> responseDto = Arrays.asList(
//                new RepliesResponseDto(replies1),
//                new RepliesResponseDto(replies2)
//        );
//
//        // eq : 특정한 값을 기대하는 경우에 사용됨
//        // any : 어떤 값이든 허용하는 경우에 사용됨
//        when(repliesController.repliesMeList(any(UserDetailsImpl.class), any(Pageable.class)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        // Wen and Then
//        mvc.perform(MockMvcRequestBuilders.get("/api/repliesme")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(responseDto))
//                        .with(csrf()) // CSRF 토큰을 요청에 포함
//                        .principal(principal)) // 가짜 사용자 principal 설정
//                        .andExpect(status().isOk())
//                        .andDo(print());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("대댓글 수정")
//    public void repliesUpdateTest() throws Exception {
//        mockUserSetup();
//
//        // Given
//        Long repliesId = 1L;
//        String contents = "대댓글";
//        RepliesRequestDto requestDto = new RepliesRequestDto(contents);
//
//        // eq : 특정한 값을 기대하는 경우에 사용됨
//        // any : 어떤 값이든 허용하는 경우에 사용됨
//        when(repliesController.repliesUpdate(eq(repliesId), any(RepliesRequestDto.class), any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new MessageResponseDto("대댓글을 수정하였습니다", 200), HttpStatus.OK));
//
//        // Wen and Then
//        mvc.perform(MockMvcRequestBuilders.put("/api/replies/" + repliesId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto))
//                        .with(csrf()) // CSRF 토큰을 요청에 포함
//                        .principal(principal)) // 가짜 사용자 principal 설정
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("대댓글 삭제")
//    public void repliesDeleteTest() throws Exception {
//        mockUserSetup();
//
//        // Given
//        Long repliesId = 1L;
//
//        // eq : 특정한 값을 기대하는 경우에 사용됨
//        // any : 어떤 값이든 허용하는 경우에 사용됨
//        when(repliesController.repliesDelete(eq(repliesId), any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new MessageResponseDto("대댓글을 삭제하였습니다", 200), HttpStatus.OK));
//
//        // Wen and Then
//        mvc.perform(MockMvcRequestBuilders.delete("/api/replies/" + repliesId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()) // CSRF 토큰을 요청에 포함
//                        .principal(principal)) // 가짜 사용자 principal 설정
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//}
//
//
