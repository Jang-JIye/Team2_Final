//package com.sparta.team2project.comments;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.team2project.comments.controller.CommentsController;
//import com.sparta.team2project.comments.dto.CommentsMeResponseDto;
//import com.sparta.team2project.comments.dto.CommentsRequestDto;
//import com.sparta.team2project.comments.dto.CommentsResponseDto;
//import com.sparta.team2project.comments.entity.Comments;
//import com.sparta.team2project.commons.config.WebSecurityConfig;
//import com.sparta.team2project.commons.dto.MessageResponseDto;
//import com.sparta.team2project.commons.security.UserDetailsImpl;
//import com.sparta.team2project.posts.entity.Posts;
//import com.sparta.team2project.replies.dto.RepliesResponseDto;
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
//import java.util.ArrayList;
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
//        controllers = {CommentsController.class},
//        // 제외 할 것 지정
//        excludeFilters = {
//                @ComponentScan.Filter(
//                        type = FilterType.ASSIGNABLE_TYPE,
//                        classes = WebSecurityConfig.class
//                )
//        }
//)
//
//class CommentsControllerTest {
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
//    CommentsController commentsController;
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
//    @DisplayName("댓글 생성")
//    public void commentsCreateTest() throws Exception {
//        mockUserSetup();
//
//        // Given
//        Long postId = 1L;
//        String contents = "댓글";
//        CommentsRequestDto requestDto = new CommentsRequestDto(contents);
//
//        // eq : 특정한 값을 기대하는 경우에 사용됨
//        // any : 어떤 값이든 허용하는 경우에 사용됨
//        when(commentsController.commentsCreate(eq(postId), any(CommentsRequestDto.class), any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new MessageResponseDto("댓글을 작성하였습니다.", 200), HttpStatus.OK));
//
//        // Wen and Then
//        mvc.perform(MockMvcRequestBuilders.post("/api/posts/"+postId+"/comments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto))
//                        .with(csrf()) // CSRF 토큰을 요청에 포함
//                        .principal(principal)) // 가짜 사용자 principal 설정
//                        .andExpect(status().isOk())
//                        .andDo(print());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("댓글 조회")
//    public void commentsListTest() throws Exception {
//
//        // Given
//        Long postId = 1L;
//        Comments comments1 = new Comments();
//        Comments comments2 = new Comments();
//
//        List<RepliesResponseDto> repliesList1 = new ArrayList<>();
//        List<RepliesResponseDto> repliesList2 = new ArrayList<>();
//
//        List<CommentsResponseDto> responseDto = Arrays.asList(
//                new CommentsResponseDto(comments1, repliesList1),
//                new CommentsResponseDto(comments2, repliesList2)
//        );
//
//        // eq : 특정한 값을 기대하는 경우에 사용됨
//        // any : 어떤 값이든 허용하는 경우에 사용됨
//        when(commentsController.commentsList(eq(postId), any(Pageable.class)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        // Wen and Then
//        mvc.perform(MockMvcRequestBuilders.get("/api/posts/" + postId + "/comments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(responseDto)))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("마이페이지에서 내가 쓴 댓글 조회")
//    public void commentsMeListTest() throws Exception {
//        mockUserSetup();
//
//        // Given
//        Comments comments1 = new Comments();
//        Comments comments2 = new Comments();
//
//        Posts posts1 = new Posts();
//        Posts posts2 = new Posts();
//
//        List<CommentsMeResponseDto> responseDto = Arrays.asList(
//                new CommentsMeResponseDto(comments1, posts1.getTitle()),
//                new CommentsMeResponseDto(comments2, posts2.getTitle())
//        );
//
//        // eq : 특정한 값을 기대하는 경우에 사용됨
//        // any : 어떤 값이든 허용하는 경우에 사용됨
//        when(commentsController.commentsMeList(any(UserDetailsImpl.class), any(Pageable.class)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        // Wen and Then
//        mvc.perform(MockMvcRequestBuilders.get("/api/commentsme")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(responseDto))
//                        .with(csrf()) // CSRF 토큰을 요청에 포함
//                        .principal(principal)) // 가짜 사용자 principal 설정
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("댓글 수정")
//    public void commentsUpdateTest() throws Exception {
//        mockUserSetup();
//
//        // Given
//        Long commentId = 1L;
//        String contents = "댓글";
//        CommentsRequestDto requestDto = new CommentsRequestDto(contents);
//
//        // eq : 특정한 값을 기대하는 경우에 사용됨
//        // any : 어떤 값이든 허용하는 경우에 사용됨
//        when(commentsController.commentsUpdate(eq(commentId), any(CommentsRequestDto.class), any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new MessageResponseDto("댓글을 수정하였습니다.", 200), HttpStatus.OK));
//
//        // Wen and Then
//        mvc.perform(MockMvcRequestBuilders.put("/api/comments/"+commentId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDto))
//                        .with(csrf()) // CSRF 토큰을 요청에 포함
//                        .principal(principal)) // 가짜 사용자 principal 설정
//                        .andExpect(status().isOk())
//                        .andDo(print());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("댓글 삭제")
//    public void commentsDeleteTest() throws Exception {
//        mockUserSetup();
//
//        // Given
//        Long commentId = 1L;
//
//        // eq : 특정한 값을 기대하는 경우에 사용됨
//        // any : 어떤 값이든 허용하는 경우에 사용됨
//        when(commentsController.commentsDelete(eq(commentId), any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new MessageResponseDto("댓글을 삭제하였습니다.", 200), HttpStatus.OK));
//
//        // Wen and Then
//        mvc.perform(MockMvcRequestBuilders.delete("/api/comments/"+commentId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .with(csrf()) // CSRF 토큰을 요청에 포함
//                        .principal(principal)) // 가짜 사용자 principal 설정
//                        .andExpect(status().isOk())
//                        .andDo(print());
//    }
//}
