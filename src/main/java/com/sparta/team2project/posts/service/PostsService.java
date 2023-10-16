package com.sparta.team2project.posts.service;

import com.sparta.team2project.comments.entity.Comments;
import com.sparta.team2project.comments.repository.CommentsRepository;
import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.commons.exceptionhandler.CustomException;
import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
import com.sparta.team2project.replies.entity.Replies;
import com.sparta.team2project.replies.repository.RepliesRepository;
import com.sparta.team2project.schedules.dto.SchedulesRequestDto;
import com.sparta.team2project.tripdate.entity.TripDate;
import com.sparta.team2project.tripdate.repository.TripDateRepository;
import com.sparta.team2project.posts.dto.*;
import com.sparta.team2project.posts.entity.Posts;
import com.sparta.team2project.posts.repository.PostsRepository;
import com.sparta.team2project.postslike.entity.PostsLike;
import com.sparta.team2project.postslike.repository.PostsLikeRepository;
import com.sparta.team2project.schedules.entity.Schedules;
import com.sparta.team2project.schedules.repository.SchedulesRepository;
import com.sparta.team2project.tags.entity.Tags;
import com.sparta.team2project.tags.repository.TagsRepository;
import com.sparta.team2project.users.UserRepository;

import com.sparta.team2project.users.Users;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PostsService {
    private final PostsRepository postsRepository;
    private final TripDateRepository tripDateRepository;
    private final SchedulesRepository schedulesRepository;
    private final PostsLikeRepository postsLikeRepository;
    private final UserRepository usersRepository;
    private final CommentsRepository commentsRepository;
    private final RepliesRepository repliesRepository;
    private final TagsRepository tagsRepository;

    // 게시글 생성
    public MessageResponseDto createPost(TotalRequestDto totalRequestDto,Users users) {

        Users existUser = checkUser(users); // 사용자 조회

        Posts posts = new Posts(totalRequestDto.getContents(),
                totalRequestDto.getTitle(),
                totalRequestDto.getPostCategory(),
                existUser);
        postsRepository.save(posts);  //posts 저장

        List<String> tagsList = totalRequestDto.getTagsList();
        tagsList.stream()
                .map(tag -> new Tags(tag, posts))
                .forEach(tagsRepository::save); // tags 저장

        List<TripDateRequestDto> tripDateRequestDtoList = totalRequestDto.getTripDateList();
        for(TripDateRequestDto tripDateRequestDto : tripDateRequestDtoList){
            TripDate tripDate = new TripDate(tripDateRequestDto,posts);
            tripDateRepository.save(tripDate); // tripDate 저장

            List<Schedules>schedulesList=new ArrayList<>();
            for(SchedulesRequestDto schedulesDto: tripDateRequestDto.getSchedulesList()) {
                Schedules schedules = new Schedules(tripDate,schedulesDto);
                schedulesList.add(schedules);
            }
            schedulesRepository.saveAll(schedulesList);
        }
        return new MessageResponseDto("게시글이 등록 되었습니다.", HttpServletResponse.SC_OK);
    }

    // 단일 게시물 조회
    public PostResponseDto getPost(Long postId) {

        Posts posts = checkPosts(postId); // 게시물 id 조회

        posts.viewCount();// 조회수 증가 시키는 메서드
        int commentNum = commentsRepository.countByPosts(posts); // 댓글 세는 메서드

        List<Tags> tag = tagsRepository.findByPosts(posts); // 해당 게시물 관련 태그 조회

        List<String> tagsList = new ArrayList<>();
        for (Tags tags:tag){
            tagsList.add(tags.getPurpose());
        }
//    ---------------------------------------------------
//        List<Comments> commentsList = commentsRepository.findByPostsOrderByCreatedAtDesc(posts); // 해당 게시글의 댓글 조회
//        List<PostDetailResponseDto>totalCommentRepliesDto = new ArrayList<>(); // response로 반환할 객체 리스트
//
//        for(Comments comments:commentsList){
//            List<ReplyResponseDto> repliesAboutList = new ArrayList<>(); // commentsID별 replies 객체에 대한 필드 담을 리스트
//
//            List<Replies> repliesList = repliesRepository.findAllByCommentsOrderByCreatedAtDesc(comments);// 해당 댓글 관련 replies 객체가 들어있는 리스트
//            for(Replies replies:repliesList){ // replies 객체 하나씩 빼옴
//                String contents = replies.getContents();
//                String nickName = replies.getNickname();
//                LocalDateTime createdAt = replies.getCreatedAt();
//                LocalDateTime modifiedAt = replies.getModifiedAt();
//
//                ReplyResponseDto replyResponseDto = new ReplyResponseDto(contents,nickName,createdAt,modifiedAt);
//                repliesAboutList.add(replyResponseDto);
//            }
//            PostDetailResponseDto dto = new PostDetailResponseDto(comments.getContents(),comments.getNickname(),comments.getCreatedAt(),comments.getModifiedAt(),repliesAboutList);
//            totalCommentRepliesDto.add(dto);
//        }
//---------------------------------------
        return new PostResponseDto(posts,posts.getUsers(),tagsList,commentNum);
    }

    // 게시글 전체 조회
    public Slice<PostResponseDto> getAllPosts(int page,int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<Posts> postsPage = postsRepository.findAllByOrderByModifiedAtDesc(pageable);

        return new SliceImpl<>(getPostResponseDto(postsPage.getContent()), pageable, postsPage.hasNext());
    }

    // 사용자별 게시글 전체 조회
    public List<PostResponseDto> getUserPosts(Users users) {

        List<Posts> postsList = postsRepository.findByUsersOrderByModifiedAtDesc(users);
        return getPostResponseDto(postsList);
    }


    // 키워드 검색
    public List<PostResponseDto> getKeywordPosts(String keyword){

        if(keyword==null){ // 키워드가 null값인 경우
            throw new CustomException(ErrorCode.POST_NOT_SEARCH);
        }

        // 중복을 방지하기 위한 Set 사용
        Set<Posts> postsSet = postsRepository.findByTitleContaining(keyword);
        Set<Tags> tagsSet = tagsRepository.findByPurposeContaining(keyword);
        Set<Schedules> schedulesSet = schedulesRepository.findByPlaceNameContainingOrContentsContaining(keyword,keyword);

        // Tags에서 Posts를 가져와서 추가
        tagsSet.stream()
                .map(Tags::getPosts) // Posts타입으로 변환
                .forEach(postsSet::add); // 하나씩 postsList에 삽입

        // Schedules에서 Posts를 가져와서 추가
        schedulesSet.stream()
                .map(Schedules::getTripDate) // TripDate타입으로 변환
                .map(TripDate::getPosts)  // Posts타입으로 변환
                .forEach(postsSet::add); // 하나씩 postsList에 삽입

        if (postsSet.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_EXIST);
        }

        List<Posts> postsList = new ArrayList<>(postsSet); //Set-> List로 바꿔줌

        // modifiedAt 기준으로 내림차순 정렬
        postsList.sort(Comparator.comparing(Posts::getModifiedAt).reversed());

        return getPostResponseDto(postsList);

    }

    // 랭킹 목록 조회(상위 3개)
    public List<PostResponseDto> getRankPosts() {

        // 상위 3개 게시물 가져오기 (좋아요 수 겹칠 시 modifiedAt 내림차순으로 정렬)
        List<Posts> postsList = postsRepository.findFirst3ByOrderByLikeNumDescModifiedAtDesc();
        return getPostResponseDto(postsList);
    }

    // 게시글 좋아요 및 좋아요 취소
    public MessageResponseDto like(Long id, Users users){
        Posts posts = checkPosts(id); // 게시글 조회

        Users existUser = checkUser(users); // 사용자 조회

        PostsLike overlap = postsLikeRepository.findByPostsAndUsers(posts,existUser);
        if(overlap!=null){
            postsLikeRepository.delete(overlap); // 좋아요 삭제
            posts.unlike(); // 해당 게시물 좋아요 취소시키는 메서드
            return new MessageResponseDto("좋아요 취소",HttpServletResponse.SC_OK);
        }
        else{
            PostsLike postsLike = new PostsLike(posts,existUser);
            postsLikeRepository.save(postsLike); // 좋아요 저장
            posts.like(); // 해당 게시물 좋아요수 증가시키는 메서드
            return new MessageResponseDto("좋아요 확인",HttpServletResponse.SC_OK);
        }
    }

    // 게시글 수정
    public MessageResponseDto updatePost(Long postId, UpdateRequestDto updateRequestDto,Users users) {
        Posts posts = checkPosts(postId); // 게시글 조회
        Users existUser = checkUser(users); // 사용자 조회
        checkAuthority(existUser,posts.getUsers()); //권한 확인 (ROLE 확인 및 게시글 사용자 id와 토큰에서 가져온 사용자 id 일치 여부 확인)
        posts.update(updateRequestDto); // 수정

        List<Tags> tagList = tagsRepository.findByPosts(posts); // 기존 게시물 태그 삭제
        tagsRepository.deleteAll(tagList);

        List<String> tagsList=updateRequestDto.getTagsList();
        tagsList.stream()
                .map(tag -> new Tags(tag, posts))
                .forEach(tagsRepository::save); // 수정된 tags 저장
        return new MessageResponseDto("수정 되었습니다.",HttpServletResponse.SC_OK);
    }

    // 해당 게시물 삭제
    public MessageResponseDto deletePost(Long postId, Users users){
        Posts posts = checkPosts(postId); // 게시글 조회
        Users existUser = checkUser(users); // 사용자 조회
        checkAuthority(existUser,posts.getUsers()); //권한 확인(ROLE 확인 및 게시글 사용자 id와 토큰에서 가져온 사용자 id 일치 여부 확인)

        // 연관된 댓글 삭제(orphanRemoval기능:자동으로 대댓글 삭제)
        List<Comments> commentsList = commentsRepository.findByPosts(posts);
        commentsRepository.deleteAll(commentsList);

        // 연관된 좋아요 테이블 삭제
        List<PostsLike> postsLikeList = postsLikeRepository.findByPosts(posts);
        postsLikeRepository.deleteAll(postsLikeList);

        // 연관된 테그 테이블 삭제
        List<Tags> tagsList = tagsRepository.findByPosts(posts);
        tagsRepository.deleteAll(tagsList);

        // 연관된 여행일자들 삭제(CascadeType.REMOVE기능:자동으로 여행 세부일정들 삭제)
        List<TripDate> tripDateList = tripDateRepository.findByPosts(posts);
        tripDateRepository.deleteAll(tripDateList);

        postsRepository.delete(posts); // 게시글 삭제
        return new MessageResponseDto("삭제 되었습니다.",HttpServletResponse.SC_OK);
    }

    // 사용자 조회 메서드
    private Users checkUser (Users users) {
        return usersRepository.findByEmail(users.getEmail()).
                orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_MATCH));

    }

    // ADMIN 권한 및 이메일 일치여부 메서드
    private void checkAuthority(Users existUser,Users users){
        if (!existUser.getUserRole().equals(UserRoleEnum.ADMIN)&&!existUser.getEmail().equals(users.getEmail())) {throw new CustomException(ErrorCode.NOT_ALLOWED);
        }
    }

    // 게시글 조회 메서드
    private Posts checkPosts(Long id) {
        return postsRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));
    }

    // 전체 게시글 관련 반환 시 사용 메서드
    private List<PostResponseDto> getPostResponseDto(List<Posts> postsList) {
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for(Posts posts:postsList){

            int commentNum = commentsRepository.countByPosts(posts); // 댓글 세는 메서드

            List<Tags> tag = tagsRepository.findByPosts(posts);
            List<String> tagsList = new ArrayList<>();

            for (Tags tags:tag){
                tagsList.add(tags.getPurpose());
            }
            postResponseDtoList.add(new PostResponseDto(posts,tagsList,posts.getUsers(),commentNum));
        }
        return postResponseDtoList;
    }
}