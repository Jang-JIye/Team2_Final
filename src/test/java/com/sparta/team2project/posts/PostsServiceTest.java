//package com.sparta.team2project.posts;
//
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.sparta.team2project.comments.repository.CommentsRepository;
//import com.sparta.team2project.commons.dto.MessageResponseDto;
//import com.sparta.team2project.commons.entity.UserRoleEnum;
//import com.sparta.team2project.commons.exceptionhandler.CustomException;
//import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
//import com.sparta.team2project.posts.dto.*;
//import com.sparta.team2project.posts.entity.PostCategory;
//import com.sparta.team2project.posts.entity.Posts;
//import com.sparta.team2project.posts.repository.PostsPicturesRepository;
//import com.sparta.team2project.posts.repository.PostsRepository;
//import com.sparta.team2project.posts.service.PostsService;
//import com.sparta.team2project.postslike.entity.PostsLike;
//import com.sparta.team2project.postslike.repository.PostsLikeRepository;
//import com.sparta.team2project.s3.AmazonS3ResourceStorage;
//import com.sparta.team2project.tags.entity.Tags;
//import com.sparta.team2project.tags.repository.TagsRepository;
//import com.sparta.team2project.tripdate.repository.TripDateRepository;
//import com.sparta.team2project.users.UserRepository;
//import com.sparta.team2project.users.Users;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.*;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class PostsServiceTest {
//
//    @Mock
//    private PostsRepository postsRepository;
//
//    @Mock
//    private TripDateRepository tripDateRepository;
//
//    @Mock
//    private PostsLikeRepository postsLikeRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private CommentsRepository commentsRepository;
//
//    @Mock
//    private TagsRepository tagsRepository;
//
//    @Mock
//    private AmazonS3ResourceStorage amazonS3ResourceStorage;
//
//    @Mock
//    private AmazonS3Client amazonS3Client;
//
//    @Mock
//    private PostsPicturesRepository postsPicturesRepository;
//
//    private PostsService postsService;
//
//
//
//    @BeforeEach
//    public void setup() {
//
//        postsService = new PostsService(postsRepository,tripDateRepository,postsLikeRepository,userRepository,commentsRepository,tagsRepository,amazonS3ResourceStorage,amazonS3Client,postsPicturesRepository);
//    }
//
//
//    public TripDateOnlyRequestDto MockTripDateOnlyRequestDto(){
//        TripDateOnlyRequestDto tripDateOnlyRequestDto = mock(TripDateOnlyRequestDto.class);
//        when(tripDateOnlyRequestDto.getChosenDate()).thenReturn(LocalDate.of(2023, 10, 10));
//        return tripDateOnlyRequestDto;
//    }
//
//    public TotalRequestDto MockTotalRequestDto(){
//        TotalRequestDto totalRequestDto = mock(TotalRequestDto.class);
//        when(totalRequestDto.getTitle()).thenReturn("제목");
//        when(totalRequestDto.getContents()).thenReturn("내용");
//        when(totalRequestDto.getPostCategory()).thenReturn(PostCategory.연인);
//        when(totalRequestDto.getSubTitle()).thenReturn("부제목");
//        List<String> tagsList = new ArrayList<>();
//        tagsList.add("태그1");
//        tagsList.add("태그2");
//        when(totalRequestDto.getTagsList()).thenReturn(tagsList);
//        List<TripDateOnlyRequestDto> tripDateOnlyRequestDtoList = new ArrayList<>();
//        tripDateOnlyRequestDtoList.add(MockTripDateOnlyRequestDto());
//        when(totalRequestDto.getTripDateList()).thenReturn(tripDateOnlyRequestDtoList);
//
//        return totalRequestDto;
//    }
//
//    public UpdateRequestDto MockUpdateRequestDto(){
//        UpdateRequestDto updateRequestDto = mock(UpdateRequestDto.class);
//        when(updateRequestDto.getTitle()).thenReturn("제목수정");
//        when(updateRequestDto.getContents()).thenReturn("내용수정");
//        when(updateRequestDto.getPostCategory()).thenReturn(PostCategory.친구);
//        when(updateRequestDto.getSubTitle()).thenReturn("부제목수정");
//        List<String> tagsList = new ArrayList<>();
//        tagsList.add("태그1");
//        tagsList.add("태그2");
//        when(updateRequestDto.getTagsList()).thenReturn(tagsList);
//
//        return updateRequestDto;
//    }
//
//    public Users MockUsers(){
//        return new Users("testuser@example.com", "user", "Password!", UserRoleEnum.USER, "image/profileImg.png");
//    }
//
//    public Users MockUsers1(){
//        return new Users("testuser1@example.com", "user1", "Password!!", UserRoleEnum.USER, "image/profileImg1.png");
//    }
//
//    public Users MockAdminUsers(){
//        return new Users("adminuser@example.com", "admin", "Password1!", UserRoleEnum.ADMIN, "image/profileImg.png");
//    }
//
//    public Posts MockPosts(){
//        return new Posts("내용1", "제목1", PostCategory.가족, "부제목1", MockUsers());
//    }
//
//
//    public List<Tags> MockTags(){
//        List<Tags> MocktagList = new ArrayList<>();
//          MocktagList.add(new Tags("태그1",MockPosts()));
//          MocktagList.add(new Tags("태그2",MockPosts()));
//        return MocktagList;
//    }
//    @Test
//    @DisplayName("게시글 작성 Test")
//    public void testCreatePost() {
//        // 가짜 사용자 데이터 생성
//        Users testUser = MockUsers();
//
//        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(java.util.Optional.of(testUser));
//
//        Users usersReturn = userRepository.findByEmail(testUser.getEmail()).orElseThrow(
//                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//        );
//
//        TotalRequestDto fakeDto = MockTotalRequestDto();
//
//        // 테스트 메서드 호출
//        PostMessageResponseDto response = postsService.createPost(fakeDto, testUser);
//
//        // 결과 검증
//        assertEquals("게시글이 등록 되었습니다.", response.getMsg());
//    }
//    @Test
//    @DisplayName("단일 게시물 조회 Test")
//    public void testGetPost() {
//        // Posts 객체의 목(mock)을 생성
//        Posts posts = MockPosts();
//
//        // 샘플 postId를 생성
//        Long postId = 1L;
//
//
//        when(postsRepository.findById(postId)).thenReturn(Optional.of(posts));
//        when(commentsRepository.countByPosts(posts)).thenReturn(5); // 댓글 수의 예제
//
//        // 테스트하려는 메서드를 실행
//        PostResponseDto result = postsService.getPost(postId);
//
//        // 결과를 확인
//        assertNotNull(result);
//        assertEquals(posts.getPostCategory(), result.getPostCategory());
//
//    }
//    @Test
//    @DisplayName("유효하지 않은 게시글 조회시 예외 Test")
//    public void testInvalidGetPost() {
//        Long postId = 1L;
//
//        // 목(mock) 객체의 예상 동작을 설정하여 findById 메서드가 예외를 던짐
//        when(postsRepository.findById(postId)).thenReturn(java.util.Optional.empty());
//
//        // 예외가 발생하는 메서드를 호출
//        CustomException exception = assertThrows(CustomException.class, () -> postsService.getPost(postId));
//        assertEquals(ErrorCode.POST_NOT_EXIST, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("등록되지 않은 유저 예외 Test")
//    public void InvalidUsers() {
//        Users testUser = MockUsers();
//
//        // 예외가 발생해야 하는 상황을 설정 (예: 사용자가 없는 경우)
//        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(java.util.Optional.empty());
//
//        // 예외가 발생하는 메서드를 호출하고 예외를 확인
//        CustomException exception = assertThrows(CustomException.class, () -> postsService.getUserPosts(testUser));
//        assertEquals(ErrorCode.ID_NOT_MATCH, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("전체 게시글 조회 Test")
//    public void testGetAllPosts() {
//        // 가짜 Posts 객체 목록을 생성합니다.
//        List<Posts> mockPostsList = new ArrayList<>();
//        mockPostsList.add(MockPosts());
//        mockPostsList.add(MockPosts());
//        mockPostsList.add(MockPosts());
//        mockPostsList.add(MockPosts());
//
//        // 페이징 관련 설정을 정의합니다.
//        int page = 0;
//        int size = 3;
//        Pageable pageable = PageRequest.of(page, size);
//
//        // 가짜 Page 객체를 생성합니다.
//        Page<Posts> fakePage = new PageImpl<>(mockPostsList,pageable,mockPostsList.size());
//
//        // 목(mock) 객체의 예상 동작을 정의합니다.
//        when(postsRepository.findAllPosts(pageable)).thenReturn(fakePage);
//
//
//        // 테스트하려는 메서드를 실행합니다.
//        Slice<PostResponseDto> result = postsService.getAllPosts(page, size);
//
//        // 결과를 확인합니다.
//        assertNotNull(result);
//        assertTrue(result.hasNext());
//        assertEquals( mockPostsList.size(), result.getNumberOfElements());
//
//    }
//
//    @Test
//    @DisplayName("유저가 작성한 게시글들 조회 Test")
//    public void testGetUserPosts() {
//        // 가짜 사용자 데이터 생성
//        Users testUser = MockUsers();
//
//        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(java.util.Optional.of(testUser));
//
//        Users usersReturn = userRepository.findByEmail(testUser.getEmail()).orElseThrow(
//                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//        );
//
//        List<Posts> mockPostsList = new ArrayList<>();
//        mockPostsList.add(new Posts("내용1","제목1",  PostCategory.가족, "부제목", testUser));
//        mockPostsList.add(new Posts("내용2","제목2", PostCategory.혼자, "부제목1", testUser));
//
//        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
//        when(postsRepository.findByUsersOrderByCreatedAtDesc(testUser)).thenReturn(mockPostsList);
//
//        // 테스트 메서드 호출
//        List<PostResponseDto> response = postsService.getUserPosts(testUser);
//
//        // 결과 검증
//        assertEquals(2, response.size());
//        assertEquals("제목2", response.get(1).getTitle());
//    }
//
//
//    @Test
//    @DisplayName("검색 api Test") // 수정
//    public void testValidKeywordPosts() {
//        String keyword = "ex";
//
//        // 가짜 검색 결과를 생성
//        Set<Posts> mockPostsSet = new HashSet<>();
//        Posts post1 = new Posts("내용1", "제ex목1", PostCategory.가족, "부제목1", new Users());
//        Posts post2 = new Posts("내용2", "ex제목", PostCategory.가족, "부제목2", new Users());
//        Posts post3 = new Posts("내용3", "제목2ex", PostCategory.가족, "부제목3", new Users());
//
//        // createdAt 필드를 직접 설정
//        post1.setCreatedAt(LocalDateTime.now());
//        post2.setCreatedAt(LocalDateTime.now());
//        post3.setCreatedAt(LocalDateTime.now());
//
//        mockPostsSet.add(post1);
//        mockPostsSet.add(post2);
//        mockPostsSet.add(post3);
//
//
//        when(postsRepository.searchKeyword(keyword)).thenReturn(mockPostsSet);
//
//        List<PostResponseDto> result = postsService.getKeywordPosts(keyword);
//
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//
//        int expectedSize = 3;
//        assertEquals(expectedSize, result.size());
//    }
//    @Test
//    @DisplayName("검색 결과가 null일 경우 예외 Test")
//    public void testKeywordPostsWithNullKeyword() {
//        String keyword = null;
//
//        // 키워드가 null인 경우 예외가 발생해야 함
//        CustomException exception = assertThrows(CustomException.class, () -> postsService.getKeywordPosts(keyword));
//        assertEquals(ErrorCode.POST_NOT_SEARCH, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("검색 결과가 없을 경우 예외 Test")
//    public void testKeywordPostsWithNoResults() {
//        String keyword = "nonexistent";
//
//        // 검색 결과가 없는 경우 예외가 발생해야 함
//        CustomException exception = assertThrows(CustomException.class, () -> postsService.getKeywordPosts(keyword));
//        assertEquals(ErrorCode.POST_NOT_EXIST, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("좋아요 순으로 게시글 조회 Test") //수정
//    public void testGetRankPosts() {
//        // 가짜 게시물 데이터 생성
//        Posts post1 = new Posts("내용1", "제ex목1", PostCategory.가족, "부제목1", new Users());
//        Posts post2 = new Posts("내용2", "ex제목", PostCategory.가족, "부제목2", new Users());
//        Posts post3 = new Posts("내용3", "제목2ex", PostCategory.가족, "부제목3", new Users());
//
//
//
//        // 좋아요 수 설정
//        post1.like();
//        post1.like();
//        post2.like();
//        post2.like();
//        post2.like();
//        post3.like();
//        post3.like();
//
//
//        post1.setCreatedAt(LocalDateTime.now().minusDays(1));
//        post2.setCreatedAt(LocalDateTime.now().minusDays(2));
//        post3.setCreatedAt(LocalDateTime.now().minusDays(3));
//
//
//        List<Posts> mockPostsList = new ArrayList<>();
//        mockPostsList.add(post1);
//        mockPostsList.add(post2);
//        mockPostsList.add(post3);
//        mockPostsList.sort(Comparator.comparing(Posts::getLikeNum).reversed()
//                .thenComparing(Posts::getCreatedAt, Comparator.reverseOrder()));
//
//        // PostsRepository의 동작 설정
//        when(postsRepository.findTop10ByTitleIsNotNullAndContentsIsNotNullOrderByLikeNumDescCreatedAtDesc())
//                .thenReturn(mockPostsList);
//
//        // 테스트 메서드 호출
//        List<PostResponseDto> result = postsService.getRankPosts();
//
//        // 결과를 확인
//        assertNotNull(result);
//        assertEquals(3, result.size()); // 상위 3개 게시물이 반환되어야 합니다.
//
//        // 게시물은 좋아요 수와 createdAt 날짜에 따라 내림차순으로 정렬되어야 합니다.
//
//        assertEquals(post2.getTitle(), result.get(0).getTitle());
//        assertEquals(post1.getTitle(), result.get(1).getTitle());
//        assertEquals(post3.getTitle(), result.get(2).getTitle());
//    }
//
//    @Test
//    @DisplayName("user가 좋아요 누른 게시물 조회 Test")
//    public void testUserLikePosts() {
//        Users user = MockUsers();
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
//
//        Users usersReturn = userRepository.findByEmail(user.getEmail()).orElseThrow(
//                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//        );
//       // 가짜 Posts 객체 목록을 생성합니다.
//        List<Posts> mockPostsList = new ArrayList<>();
//        mockPostsList.add(MockPosts());
//        mockPostsList.add(MockPosts());
//        mockPostsList.add(MockPosts());
//        mockPostsList.add(MockPosts());
//
//        // 페이징 관련 설정을 정의합니다.
//        int page = 0;
//        int size = 3;
//        Pageable pageable = PageRequest.of(page, size);
//
//        // 가짜 Page 객체를 생성합니다.
//        Page<Posts> fakePage = new PageImpl<>(mockPostsList,pageable,mockPostsList.size());
//
//        // 목(mock) 객체의 예상 동작을 정의합니다.
//        when(postsRepository.findUsersLikePosts(user,pageable)).thenReturn(fakePage);
//
//
//        // 테스트하려는 메서드를 실행합니다.
//        Page<PostResponseDto> result = postsService.getUserLikePosts(user,page, size);
//
//        // 결과를 확인합니다.
//        assertNotNull(result);
//        assertTrue(result.hasNext());
//        assertEquals(mockPostsList.size(), result.getNumberOfElements());
//
//    }
//
//    @Test
//    @DisplayName("사용자가 좋아요를 누른 게시물 ID 조회 테스트")
//    public void testGetUserLikePostsId() {
//        // 가짜 사용자 생성
//        Users user = MockUsers();
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
//
//        List<Long> mockPostsIdList = new ArrayList<>();
//        mockPostsIdList.add(MockPosts().getId());
//        mockPostsIdList.add(MockPosts().getId());
//        mockPostsIdList.add(MockPosts().getId());
//        mockPostsIdList.add(MockPosts().getId());
//
//
//        // PostsRepository 목 객체의 동작을 정의하여 mockPostsIdList 반환
//        when(postsRepository.findUsersLikePostsId(user)).thenReturn(mockPostsIdList);
//
//
//        // 테스트하려는 메서드 호출
//        List<Long> result = postsService.getUserLikePostsId(user);
//
//        // 결과 확인
//        assertNotNull(result);
//        assertEquals(mockPostsIdList, result);
//    }
//
//    @Test
//    @DisplayName("좋아요 api Test")
//    public void testLikeAndUnlikePost() {
//        // 가짜 게시글 및 사용자 데이터 생성
//        Posts post = MockPosts(); // MockPost는 가상의 게시글을 생성하는 메서드로 가정
//        Users user = MockUsers(); // MockUser는 가상의 사용자를 생성하는 메서드로 가정
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
//
//        Users usersReturn = userRepository.findByEmail(user.getEmail()).orElseThrow(
//                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//        );
//
//
//        when(postsRepository.findById(post.getId())).thenReturn(java.util.Optional.of(post));
//
//        Posts postsReturn = postsRepository.findById(post.getId()).orElseThrow(
//                () -> new CustomException(ErrorCode.POST_NOT_EXIST)
//        );
//
//        // Mock 객체 동작 설정
//        when(postsRepository.findById(post.getId())).thenReturn(Optional.of(post));
//        when(postsLikeRepository.findByPostsAndUsers(post, user)).thenReturn(null); // 사용자가 아직 좋아요를 누르지 않은 상태
//
//        // 테스트 좋아요 추가
//        LikeResponseDto likeResponse = postsService.like(post.getId(), user);
//
//        // 결과 검증
//        assertEquals("좋아요 확인", likeResponse.getMsg());
//        assertTrue(likeResponse.isCheck());
//
//        // Mock 객체 동작 설정 (좋아요 취소)
//        when(postsLikeRepository.findByPostsAndUsers(post, user)).thenReturn(new PostsLike(post, user)); // 사용자가 이미 좋아요를 누른 상태
//
//        // 테스트 좋아요 취소
//        likeResponse = postsService.like(post.getId(), user);
//
//        // 결과 검증
//        assertEquals("좋아요 취소", likeResponse.getMsg());
//        assertFalse(likeResponse.isCheck());
//    }
//
//    @Test
//    @DisplayName("게시글 수정 Test")
//    public void testUpdatePost() {
//        // 가짜 게시물, 사용자 및 수정 요청 데이터 생성
//        Posts post = MockPosts(); // MockPost는 가상의 게시물을 생성하는 메서드로 가정
//        Users user = MockUsers(); // MockUser는 가상의 사용자를 생성하는 메서드로 가정
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
//
//        when(postsRepository.findById(post.getId())).thenReturn(java.util.Optional.of(post));
//
//        UpdateRequestDto updateRequestDto = MockUpdateRequestDto();
//
//        // Mock 객체 동작 설정
//        when(postsRepository.findById(post.getId())).thenReturn(Optional.of(post));
//        when(tagsRepository.findByPosts(post)).thenReturn(Collections.emptyList());
//
//        // 테스트 메서드 호출
//        MessageResponseDto response = postsService.updatePost(post.getId(), updateRequestDto, user);
//
//        // 결과 검증
//        assertEquals("수정 되었습니다.", response.getMsg());
//
//    }
//    @Test
//    @DisplayName("게시글 수정 admin권한 가진 경우 Test")
//    public void testAdminUpdatePost() {
//        // 가짜 게시물, 사용자 및 수정 요청 데이터 생성
//        Posts post = MockPosts(); // MockPost는 가상의 게시물을 생성하는 메서드로 가정
//        Users adminUsers = MockAdminUsers(); // Admin권한 가진 user
//
//        when(userRepository.findByEmail(adminUsers.getEmail())).thenReturn(java.util.Optional.of(adminUsers));
//
//        Users usersReturn = userRepository.findByEmail(adminUsers.getEmail()).orElseThrow(
//                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//        );
//
//
//        when(postsRepository.findById(post.getId())).thenReturn(java.util.Optional.of(post));
//
//        Posts postsReturn = postsRepository.findById(post.getId()).orElseThrow(
//                () -> new CustomException(ErrorCode.POST_NOT_EXIST)
//        );
//
//        UpdateRequestDto updateRequestDto = MockUpdateRequestDto();
//
//
//        // Mock 객체 동작 설정
//        when(postsRepository.findById(post.getId())).thenReturn(Optional.of(post));
//        when(tagsRepository.findByPosts(post)).thenReturn(Collections.emptyList());
//
//        // 테스트 메서드 호출
//        MessageResponseDto response = postsService.updatePost(post.getId(), updateRequestDto, adminUsers);
//
//        // 결과 검증
//        assertEquals("수정 되었습니다.", response.getMsg());
//
//    }
//
//    @Test
//    @DisplayName("admin권한도 아니면서 게시글 직접 작성한  user가 아닌 경우 예외 Test")
//    public void testCheckAuthorityWithNonAdminRole() {
//        Users existUser = MockUsers(); // 가상의 일반 사용자 생성
//        Users user = MockUsers1(); // 가상의 일반 사용자 생성
//
//        // 호출한 사용자가 일반 사용자인 경우 NOT_ALLOWED 예외가 발생해야 합니다.
//        CustomException exception = assertThrows(CustomException.class, () -> postsService.checkAuthority(existUser, user));
//        assertEquals(ErrorCode.NOT_ALLOWED, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("게시글 삭제 Test")
//    public void testDeletePost() {
//    // 가짜 게시물, 사용자 및 수정 요청 데이터 생성
//    Posts post = MockPosts(); // MockPost는 가상의 게시물을 생성하는 메서드로 가정
//    Users user = MockUsers();
//
//    when(userRepository.findByEmail(user.getEmail())).thenReturn(java.util.Optional.of(user));
//
//    Users usersReturn = userRepository.findByEmail(user.getEmail()).orElseThrow(
//            () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//    );
//
//
//    when(postsRepository.findById(post.getId())).thenReturn(java.util.Optional.of(post));
//
//    Posts postsReturn = postsRepository.findById(post.getId()).orElseThrow(
//            () -> new CustomException(ErrorCode.POST_NOT_EXIST)
//    );
//
//    // 테스트 메서드 호출
//     MessageResponseDto response = postsService.deletePost(post.getId(), user);
//
//     // 결과 검증
//     assertEquals("삭제 되었습니다.", response.getMsg());
//    }
//
//    @Test
//    @DisplayName("게시글 삭제 admin권한 가진 경우 Test")
//    public void testAdminDeletePost() {
//
//        // 가짜 게시물, 사용자 및 수정 요청 데이터 생성
//        Posts post = MockPosts(); // MockPost는 가상의 게시물을 생성하는 메서드로 가정
//        Users adminUsers = MockAdminUsers(); // Admin권한 가진 user
//
//        when(userRepository.findByEmail(adminUsers.getEmail())).thenReturn(java.util.Optional.of(adminUsers));
//
//        Users usersReturn = userRepository.findByEmail(adminUsers.getEmail()).orElseThrow(
//                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
//        );
//
//
//        when(postsRepository.findById(post.getId())).thenReturn(java.util.Optional.of(post));
//
//        Posts postsReturn = postsRepository.findById(post.getId()).orElseThrow(
//                () -> new CustomException(ErrorCode.POST_NOT_EXIST)
//        );
//        // 테스트 메서드 호출
//        MessageResponseDto response = postsService.deletePost(post.getId(), adminUsers);
//
//        // 결과 검증
//        assertEquals("삭제 되었습니다.", response.getMsg());
//    }
//
//
//}
