//package com.sparta.team2project.posts;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.team2project.commons.config.WebSecurityConfig;
//import com.sparta.team2project.commons.dto.MessageResponseDto;
//import com.sparta.team2project.commons.security.UserDetailsImpl;
//import com.sparta.team2project.posts.controller.PostsController;
//import com.sparta.team2project.posts.dto.*;
//import com.sparta.team2project.posts.entity.PostCategory;
//import com.sparta.team2project.posts.entity.Posts;
//import com.sparta.team2project.tags.entity.Tags;
//import com.sparta.team2project.users.Users;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@WebMvcTest(
//        controllers = {PostsController.class},
//        // 제외 할 것 지정
//        excludeFilters = {
//                @ComponentScan.Filter(
//                        type = FilterType.ASSIGNABLE_TYPE,
//                        classes = WebSecurityConfig.class
//                )
//        }
//)
//@AutoConfigureWebMvc
//@MockBean(JpaMetamodelMappingContext.class)
//class PostsControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    private Principal principal;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    PostsController postsController;
//
//    private void setup() {
//        UserDetails userDetails = User.withUsername("testuser@example.com")
//                .password("Password1!")
//                .roles("USER") // 롤에 대한 설정
//                .build();
//        principal = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());
//
//    }
//
//
//
//    @WithMockUser
//    @Test
//    @DisplayName("게시글 작성 Test")
//    public void createPostTest() throws Exception {
//        setup();
//        Posts posts = new Posts();
//        List<Long> idList = new ArrayList<>();
//
//        // JSON 요청 본문을 준비합니다.
//        String title=null;
//        String contents=null;
//        PostCategory postCategory = PostCategory.연인;
//        String subTitle="부제목1입니다.";
//        List<String>tagsList=new ArrayList<>();
//        tagsList.add("태그4");
//        tagsList.add("태그3");
//        List<TripDateOnlyRequestDto> tripDateList=new ArrayList<>();
//        TripDateOnlyRequestDto dto1 = new TripDateOnlyRequestDto(LocalDate.parse("2023-10-22", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        TripDateOnlyRequestDto dto2 = new TripDateOnlyRequestDto(LocalDate.parse("2023-10-23", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        tripDateList.add(dto1);
//        tripDateList.add(dto2);
//        TotalRequestDto totalRequestDto = new TotalRequestDto(title,contents,postCategory,subTitle,tagsList,tripDateList);
//
//        when(postsController.createPost(any(TotalRequestDto.class),any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new PostMessageResponseDto("게시글이 등록 되었습니다.", HttpServletResponse.SC_OK,posts,idList), HttpStatus.OK));
//
//        // JSON 요청 본문을 포함하여 "/api/posts" 엔드포인트로 POST 요청을 수행합니다.
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(totalRequestDto))
//                        .with(csrf())
//                        .principal(principal))
//                .andExpect(status().isOk()); // 응답 상태가 OK (200)인지 확인합니다.
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("게시글 단일 조회 Test")
//    public void getOnePostTest() throws Exception {
//
//        // Given
//        Long postId = 1L;
//        Posts post = new Posts();
//
//
//        when(postsController.getPost(eq(postId)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/" + postId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(post)))
//                .andExpect(status().isOk());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("게시글 전체 조회 Test")
//    public void getAllPostTest() throws Exception {
//
//        // Given
//        int page = 0;
//        int size = 3;
//
//        List<Posts> responseDto = Arrays.asList(new Posts(),new Posts(),new Posts(),new Posts());
//
//        when(postsController.getAllPosts(eq(page),eq(size)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(responseDto)))
//              .andExpect(status().isOk());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("사용자별 게시글 전체 조회 Test")
//    public void getUserPostTest() throws Exception {
//        setup();
//
//        List<Posts> responseDto = Arrays.asList(new Posts(),new Posts(),new Posts(),new Posts());
//
//        when(postsController.getUserPosts(any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(responseDto))
//                        .with(csrf())
//                        .principal(principal))
//                .andExpect(status().isOk());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("게시글 검색 Test")
//    public void getKeywordPost() throws Exception {
//
//        String keyword = "test";
//        Posts post1 = new Posts("내용1", "제목1", PostCategory.가족, "부제목1", new Users());
//        Posts post2 = new Posts("내용1", "test", PostCategory.가족, "부제목1", new Users());
//        Posts post3 = new Posts("내용1", "제목1", PostCategory.가족, "부제목1", new Users());
//        List<Tags> tagsList = Arrays.asList(new Tags(),new Tags(),new Tags());
//
//        List<PostResponseDto> responseDto= Arrays.asList(
//                new PostResponseDto(post1,tagsList,new Users(),1),
//                new PostResponseDto(post2,tagsList,new Users(),2),
//                new PostResponseDto(post3,tagsList,new Users(),3));
//
//
//        when(postsController. getKeywordPost(eq(keyword)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/search")
//                        .param("keyword", keyword)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(responseDto)))
//                .andExpect(status().isOk());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("좋아요 순 게시글 조회 Test")
//    public void getRankPosts() throws Exception {
//
//        Posts post1 = new Posts();
//        Posts post2 = new Posts();
//        Posts post3 = new Posts();
//
//        List<Tags> tagsList = Arrays.asList(new Tags(),new Tags(),new Tags());
//
//        List<PostResponseDto> responseDto= Arrays.asList(
//                // post,tagsList/users/likeNum
//                new PostResponseDto(post1,tagsList,new Users(),1),
//                new PostResponseDto(post2,tagsList,new Users(),2),
//                new PostResponseDto(post3,tagsList,new Users(),3));
//
//
//        when(postsController. getRankPosts())
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/rank")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(responseDto)))
//                .andExpect(status().isOk());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("사용자가 좋아요 한 게시글 조회 Test")
//    public void getUserLikePosts() throws Exception {
//        setup();
//        int page = 0;
//        int size = 3;
//
//        List<Posts> responseDto = Arrays.asList(new Posts(),new Posts(),new Posts(),new Posts());
//
//        when(postsController.getUserLikePosts(eq(page),eq(size),any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/rank")
//                        .param("page", String.valueOf(page))
//                        .param("size", String.valueOf(size))
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(responseDto))
//                        .with(csrf())
//                        .principal(principal))
//                .andExpect(status().isOk());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("사용자가 좋아요 누른 게시글 id만 조회 Test")
//    public void getUserLikePostsId() throws Exception {
//        setup();
//
//        Posts post1 = new Posts("내용1", "제목1", PostCategory.가족, "부제목1", new Users());
//        Posts post2 = new Posts("내용2", "제목2", PostCategory.친구, "부제목2", new Users());
//        Posts post3 = new Posts("내용3", "제목3", PostCategory.연인, "부제목3", new Users());
//
//        List<Long> responseDto = Arrays.asList(post1.getId(),post2.getId(),post3.getId());
//
//
//
//        when(postsController.getUserLikePostsId(any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/postlike/id")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(responseDto))
//                        .with(csrf())
//                        .principal(principal))
//                .andExpect(status().isOk());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("좋아요 기능 Test")
//    public void like() throws Exception {
//        setup();
//        Long postId = 1L;
//
//        Posts post = new Posts("내용1", "제목1", PostCategory.가족, "부제목1", new Users());
//
//
//        when(postsController.like(eq(postId),any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new LikeResponseDto("좋아요 확인", HttpServletResponse.SC_OK,true),HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/like/"+ postId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(post))
//                        .with(csrf())
//                        .principal(principal))
//                .andExpect(status().isOk());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("게시글 수정 Test")
//    public void updatePost() throws Exception {
//        setup();
//        Long postId = 1L;
//
//        String title = "제목 수정";
//        String contents ="내용 수정";
//        PostCategory postCategory = PostCategory.연인;
//        String subTitle="부제목 수정";
//        List<String>tagsList=new ArrayList<>();
//        tagsList.add("태그 수정");
//        tagsList.add("수정 태그");
//
//        UpdateRequestDto updateRequestDto = new UpdateRequestDto(title,contents,subTitle,postCategory,tagsList);
//
//        when(postsController.updatePost(eq(postId),any(UpdateRequestDto.class),any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new MessageResponseDto("수정 되었습니다.", HttpServletResponse.SC_OK),HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/posts/"+ postId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequestDto))
//                        .with(csrf())
//                        .principal(principal))
//                .andExpect(status().isOk());
//    }
//
//    @WithMockUser
//    @Test
//    @DisplayName("게시글 수정 Test")
//    public void deletePost() throws Exception {
//        setup();
//        Long postId = 1L;
//
//        Posts posts = new Posts();
//
//        when(postsController.deletePost(eq(postId),any(UserDetailsImpl.class)))
//                .thenReturn(new ResponseEntity<>(new MessageResponseDto("삭제 되었습니다.", HttpServletResponse.SC_OK),HttpStatus.OK));
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/posts/"+ postId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(posts))
//                        .with(csrf())
//                        .principal(principal))
//                .andExpect(status().isOk());
//    }
//
//}
