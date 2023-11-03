//package com.sparta.team2project.replies;
//
//import com.sparta.team2project.comments.dto.CommentsRequestDto;
//import com.sparta.team2project.comments.entity.Comments;
//import com.sparta.team2project.comments.repository.CommentsRepository;
//import com.sparta.team2project.commons.dto.MessageResponseDto;
//import com.sparta.team2project.commons.entity.UserRoleEnum;
//import com.sparta.team2project.commons.exceptionhandler.CustomException;
//import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
//import com.sparta.team2project.posts.entity.PostCategory;
//import com.sparta.team2project.posts.entity.Posts;
//import com.sparta.team2project.replies.dto.RepliesMeResponseDto;
//import com.sparta.team2project.replies.dto.RepliesRequestDto;
//import com.sparta.team2project.replies.dto.RepliesResponseDto;
//import com.sparta.team2project.replies.entity.Replies;
//import com.sparta.team2project.replies.repository.RepliesRepository;
//import com.sparta.team2project.replies.service.RepliesService;
//import com.sparta.team2project.users.UserRepository;
//import com.sparta.team2project.users.Users;
//import jakarta.servlet.http.HttpServletResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.domain.SliceImpl;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class RepliesServiceTest {
//
//    // @Mock 이 붙은 목 객체를 주입시킬 수 있다
//    @InjectMocks
//    private RepliesService repliesService;
//
//    // 로직이 삭제된 빈껍데기, 실제로 메서드는 가지고 있지만 내부구현이 없음
//    @Mock
//    private RepliesRepository repliesRepository;
//
//    @Mock
//    private CommentsRepository commentsRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    public CommentsRequestDto MockCommentsRequestDto() {
//        CommentsRequestDto commentsRequestDto = mock(CommentsRequestDto.class);
//        when(commentsRequestDto.getContents()).thenReturn("댓글");
//        return commentsRequestDto;
//    }
//
//    public RepliesRequestDto MockRepliesRequestDto() {
//        RepliesRequestDto repliesRequestDto = mock(RepliesRequestDto.class);
//        when(repliesRequestDto.getContents()).thenReturn("대댓글");
//        return repliesRequestDto;
//    }
//
//    public Users MockUsers1() {
//        return new Users("rkawk@email.com", "감자", "test123!", UserRoleEnum.USER, "image/profileImg.png");
//    }
//
//    public Users MockUsers2() {
//        return new Users("qksksk@email.com", "바나나", "test123!", UserRoleEnum.USER, "image/profileImg.png");
//    }
//
//    public Users MockAdmin() {
//        return new Users("rhksflwk@email.com", "관리자", "test123!", UserRoleEnum.ADMIN, "image/profileImg.png");
//    }
//
//    public Posts MockPosts() {
//        return new Posts("해돋이 보러간다", "정동진 해돋이", PostCategory.가족, "동해안 해돋이", MockUsers1());
//    }
//
//    public Comments MockComments() {
//        return new Comments(MockCommentsRequestDto(), MockUsers1(), MockPosts());
//    }
//
//    public Replies MockResplies() {
//        return new Replies(MockRepliesRequestDto(), MockUsers1(), MockComments());
//    }
//
//    @Test
//    @DisplayName("[정상 작동] 대댓글 생성")
//    public void repliesCreateTest() {
//
//        // Given
//        Long commentId = 1L;
//        RepliesRequestDto requestDto = MockRepliesRequestDto();
//        Users users = MockUsers1(); // 사용자 정보 초기화
//        Comments comments = new Comments(); // 게시글 정보 초기화
//
//        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comments));
//        when(repliesRepository.save(any(Replies.class))).thenReturn(new Replies());
//
//        // When
//        MessageResponseDto response = repliesService.repliesCreate(commentId, requestDto, users);
//
//        // Then
//        System.out.println("대댓글 생성");
//        assertEquals("대댓글을 작성하였습니다", response.getMsg());
//        assertEquals(HttpServletResponse.SC_OK, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("[비정상 작동]대댓글 생성 (존재하지 않는 댓글)")
//    public void repliesCreatePostNotFoundTest() {
//
//        // Given
//        Long commentId = 1L;
//        RepliesRequestDto requestDto = MockRepliesRequestDto();
//        Users users = MockUsers1(); // 사용자 정보 초기화
//
//        when(commentsRepository.findById(commentId)).thenReturn(Optional.empty());
//
//        // When
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            repliesService.repliesCreate(commentId, requestDto, users);
//        });
//
//        // Then
//        System.out.println("ErrorCode.COMMENTS_NOT_EXIST: " + "존재하지 않는 댓글입니다");
//        System.out.println("exception.getErrorCode(): " + exception.getErrorCode());
//        assertEquals(ErrorCode.COMMENTS_NOT_EXIST, exception.getErrorCode());
//    }
////    @Test
////    @DisplayName("[정상 작동] 대댓글 조회 (내가 쓴 게시글에 댓글 작성시)")
////    public void repliesListMyPostTest() {
////
////        // Given
////        Long commentId = 1L;
////        PageRequest pageable = PageRequest.of(0, 2);
////        Users users = MockUsers1(); // 사용자 정보 초기화
////        Posts posts = MockPosts(); // 게시글 정보 초기화
////        Comments comments = MockComments(); // 게시글 정보 초기화
////
////        List<Replies> mockRepliesList = new ArrayList<>();
////        mockRepliesList.add(MockResplies());
////        mockRepliesList.add(MockResplies());
////        mockRepliesList.add(MockResplies());
////
////        Slice<Replies> fakeSlice;
////        if (mockRepliesList.size() <= pageable.getPageSize()) {
////            fakeSlice = new SliceImpl<>(mockRepliesList, pageable, false);
////        } else {
////            fakeSlice = new SliceImpl<>(mockRepliesList, pageable, true);
////        }
////
////        when(userRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(posts.getUsers()));
////        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comments));
////        when(repliesRepository.findByComments_IdOrderByCreatedAtDesc(commentId, pageable)).thenReturn(fakeSlice);
////
////        // When
////        Slice<RepliesResponseDto> response = repliesService.repliesList(commentId, pageable);
////
////        // Then
////        System.out.println("내가 쓴 게시글에 글쓴이 나옴");
////        // 결과를 확인합니다.
////        assertNotNull(response);
////        assertTrue(response.hasNext());
////        assertEquals(mockRepliesList.size(), response.getNumberOfElements());
////    }
////
////    @Test
////    @DisplayName("[정상 작동] 대댓글 조회")
////    public void repliesListTest() {
////
////        // Given
////        Long commentid = 1L;
////        PageRequest pageable = PageRequest.of(0, 2);
////        Comments comments = MockComments(); // 게시글 정보 초기화
////
////        List<Replies> mockRepliesList = new ArrayList<>();
////        mockRepliesList.add(MockResplies());
////        mockRepliesList.add(MockResplies());
////        mockRepliesList.add(MockResplies());
////
////        Slice<Replies> fakeSlice;
////        if (mockRepliesList.size() <= pageable.getPageSize()) {
////            fakeSlice = new SliceImpl<>(mockRepliesList, pageable, false);
////        } else {
////            fakeSlice = new SliceImpl<>(mockRepliesList, pageable, true);
////        }
////
////        when(commentsRepository.findById(commentid)).thenReturn(Optional.of(comments));
////        when(repliesRepository.findByComments_IdOrderByCreatedAtDesc(commentid, pageable)).thenReturn(fakeSlice);
////
////        // When
////        Slice<RepliesResponseDto> response = repliesService.repliesList(commentid, pageable);
////
////        // Then
////        System.out.println("대댓글 조회");
////        // 결과를 확인합니다.
////        assertNotNull(response);
////        assertTrue(response.hasNext());
////        assertEquals(mockRepliesList.size(), response.getNumberOfElements());
////    }
////
////    @Test
////    @DisplayName("[비정상 작동]대댓글 조회 (존재하지 않는 댓글)")
////    public void repliesListCommentsNotExistTest() {
////
////        // Given
////        Long commentId = 1L;
////        PageRequest pageable = PageRequest.of(0, 2);
////
////        when(commentsRepository.findById(commentId)).thenReturn(Optional.empty());
////
////        /// When
////        CustomException exception = assertThrows(CustomException.class, () -> {
////            repliesService.repliesList(commentId, pageable);
////        });
////
////        // Then
////        System.out.println("ErrorCode.COMMENTS_NOT_EXIST: " + "존재하지 않는 댓글입니다");
////        System.out.println("exception.getErrorCode(): " + exception.getErrorCode());
////        assertEquals(ErrorCode.COMMENTS_NOT_EXIST, exception.getErrorCode());
////    }
////
////    @Test
////    @DisplayName("[비정상 작동]대댓글 조회 (존재하지 않는 대댓글)")
////    public void repliesListRepliesNotExistTest() {
////
////        // Given
////        Long commentId = 1L;
////        PageRequest pageable = PageRequest.of(0, 2);
////        Comments comments = MockComments(); // 게시글 정보 초기화
////
////        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comments));
////        when(repliesRepository.findByComments_IdOrderByCreatedAtDesc(commentId, pageable)).thenReturn(new SliceImpl<>(Collections.emptyList()));
////
////
////        /// When
////        CustomException exception = assertThrows(CustomException.class, () -> {
////            repliesService.repliesList(commentId, pageable);
////        });
////
////        // Then
////        System.out.println("ErrorCode.REPLIES_NOT_EXIST: " + "존재하지 않는 대댓글입니다");
////        System.out.println("exception.getErrorCode(): " + exception.getErrorCode());
////        assertEquals(ErrorCode.REPLIES_NOT_EXIST, exception.getErrorCode());
////    }
//
//    @Test
//    @DisplayName("[정상 작동] 내가 쓴 대댓글 조회")
//    public void repliesMeListTest() {
//
//        // Given
//        Users users = MockUsers1(); // 사용자 정보 초기화
//        PageRequest pageable = PageRequest.of(0, 2);
//
//        List<Replies> mockRepliesList = new ArrayList<>();
//        mockRepliesList.add(MockResplies());
//        mockRepliesList.add(MockResplies());
//        mockRepliesList.add(MockResplies());
//
//        Slice<Replies> fakeSlice;
//        if (mockRepliesList.size() <= pageable.getPageSize()) {
//            fakeSlice = new SliceImpl<>(mockRepliesList, pageable, false);
//        } else {
//            fakeSlice = new SliceImpl<>(mockRepliesList, pageable, true);
//        }
//
//        when(repliesRepository.findAllByAndEmailOrderByCreatedAtDesc(users.getEmail(), pageable)).thenReturn(fakeSlice);
//
//        // When
//        Slice<RepliesMeResponseDto> response = repliesService.repliesMeList(users, pageable);
//
//        // Then
//        System.out.println("내가 쓴 댓글 조회");
//        // 결과를 확인합니다.
//        assertNotNull(response);
//        assertTrue(response.hasNext());
//        assertEquals(mockRepliesList.size(), response.getNumberOfElements());
//    }
//
//    @Test
//    @DisplayName("[비정상 작동] 내가 쓴 대댓글 조회 (존재하지 않는 대댓글)")
//    public void repliesMeListRepliesNotExist() {
//
//        // Given
//        Users users = MockUsers1(); // 사용자 정보 초기화
//        PageRequest pageable = PageRequest.of(0, 2);
//
//        when(repliesRepository.findAllByAndEmailOrderByCreatedAtDesc(users.getEmail(), pageable)).thenReturn(new SliceImpl<>(Collections.emptyList()));
//
//        // When
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            repliesService.repliesMeList(users, pageable);
//        });
//
//        // Then
//        System.out.println("ErrorCode.REPLIES_NOT_EXIST: " + "존재하지 않는 대댓글입니다");
//        System.out.println("exception.getErrorCode(): " + exception.getErrorCode());
//        assertEquals(ErrorCode.REPLIES_NOT_EXIST, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("[정상 작동] 본인이 쓴 대댓글 수정 USER")
//    public void repliesUpdateUserTest() {
//
//        // Given
//        Long reliesId = 1L;
//        RepliesRequestDto requestDto = MockRepliesRequestDto();
//        Users users = MockUsers1(); // 사용자 정보 초기화
//        Posts posts = MockPosts(); // 게시글 정보 초기화
//        Replies replies = MockResplies();
//
//        when(repliesRepository.findById(reliesId)).thenReturn(Optional.of(replies));
//        when(userRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(posts.getUsers()));
//
//        // When
//        MessageResponseDto response = repliesService.repliesUpdate(reliesId, requestDto, users);
//
//        // Then
//        System.out.println("대댓글 수정");
//        assertNotNull(response);
//        assertEquals("대댓글을 수정하였습니다", response.getMsg());
//        assertEquals(HttpServletResponse.SC_OK, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("[정상 작동] 모든 대댓글 수정 ADMIN")
//    public void repliesUpdateAdminTest() {
//
//        // Given
//        Long reliesId = 1L;
//        RepliesRequestDto requestDto = MockRepliesRequestDto();
//        Users users = MockUsers1(); // 사용자 정보 초기화
//        Users users1 = MockAdmin(); // 사용자 정보 초기화
//        Posts posts = MockPosts(); // 게시글 정보 초기화
//        Replies replies = MockResplies();
//
//        when(repliesRepository.findById(reliesId)).thenReturn(Optional.of(replies));
//        when(userRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(posts.getUsers()));
//
//        // When
//        MessageResponseDto response = repliesService.repliesUpdate(reliesId, requestDto, users1);
//
//        // Then
//        System.out.println("관리자 대댓글 수정");
//        assertNotNull(response);
//        assertEquals("관리자가 대댓글을 수정하였습니다", response.getMsg());
//        assertEquals(HttpServletResponse.SC_OK, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("[비정상 작동]대댓글 수정 본인이 쓴 대댓글 아닌 경우")
//    public void repliesUpdateNotAllowedTest() {
//
//        // Given
//        Long repliesId = 1L;
//        RepliesRequestDto requestDto = MockRepliesRequestDto();
//        Users users2 = MockUsers2(); // 사용자 정보 초기화
//        Replies replies = MockResplies(); // 댓글 정보 초기화
//
//        when(replies.getEmail()).thenReturn("다른 사용자의 이메일");
//        when(repliesRepository.findById(repliesId)).thenReturn(Optional.of(replies));
//
//        // When
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            repliesService.repliesUpdate(repliesId, requestDto, users2);
//        });
//
//        // Then
//        System.out.println("ErrorCode.NOT_ALLOWED: " + "권한이 없습니다");
//        System.out.println("exception.getErrorCode(): " + exception.getErrorCode());
//        assertEquals(ErrorCode.NOT_ALLOWED, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("[정상 작동] 본인이 쓴 대댓글 삭제 USER")
//    public void repliesDeleteUserTest() {
//
//        // Given
//        Long reliesId = 1L;
//        Users users = MockUsers1(); // 사용자 정보 초기화
//        Posts posts = MockPosts(); // 게시글 정보 초기화
//        Replies replies = MockResplies();
//
//        when(repliesRepository.findById(reliesId)).thenReturn(Optional.of(replies));
//        when(userRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(posts.getUsers()));
//
//        // When
//        MessageResponseDto response = repliesService.repliesDelete(reliesId, users);
//
//        // Then
//        System.out.println("대댓글 삭제");
//        assertNotNull(response);
//        assertEquals("대댓글을 삭제하였습니다", response.getMsg());
//        assertEquals(HttpServletResponse.SC_OK, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("[정상 작동] 모든 대댓글 삭제 ADMIN")
//    public void repliesDeleteAdminTest() {
//
//        // Given
//        Long reliesId = 1L;
//        Users users = MockUsers1(); // 사용자 정보 초기화
//        Users users1 = MockAdmin(); // 사용자 정보 초기화
//        Posts posts = MockPosts(); // 게시글 정보 초기화
//        Replies replies = MockResplies();
//
//        when(repliesRepository.findById(reliesId)).thenReturn(Optional.of(replies));
//        when(userRepository.findByEmail(users.getEmail())).thenReturn(Optional.of(posts.getUsers()));
//
//        // When
//        MessageResponseDto response = repliesService.repliesDelete(reliesId, users1);
//
//        // Then
//        System.out.println("관리자 대댓글 삭제");
//        assertNotNull(response);
//        assertEquals("관리자가 대댓글을 삭제하였습니다", response.getMsg());
//        assertEquals(HttpServletResponse.SC_OK, response.getStatusCode());
//    }
//
//    @Test
//    @DisplayName("[비정상 작동]대댓글 삭제 본인이 쓴 대댓글 아닌 경우")
//    public void repliesDeleteNotAllowedTest() {
//
//        // Given
//        Long repliesId = 1L;
//        Users users2 = MockUsers2(); // 사용자 정보 초기화
//        Replies replies = MockResplies(); // 댓글 정보 초기화
//
//        when(replies.getEmail()).thenReturn("다른 사용자의 이메일");
//        when(repliesRepository.findById(repliesId)).thenReturn(Optional.of(replies));
//
//        // When
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            repliesService.repliesDelete(repliesId, users2);
//        });
//
//        // Then
//        System.out.println("ErrorCode.NOT_ALLOWED: " + "권한이 없습니다");
//        System.out.println("exception.getErrorCode(): " + exception.getErrorCode());
//        assertEquals(ErrorCode.NOT_ALLOWED, exception.getErrorCode());
//    }
//}
