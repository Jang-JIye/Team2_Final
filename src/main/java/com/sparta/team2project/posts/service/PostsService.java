package com.sparta.team2project.posts.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.sparta.team2project.comments.entity.Comments;
import com.sparta.team2project.comments.repository.CommentsRepository;
import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.commons.exceptionhandler.CustomException;
import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
import com.sparta.team2project.posts.dto.PostsPicturesResponseDto;
import com.sparta.team2project.posts.dto.PostsPicturesUploadResponseDto;
import com.sparta.team2project.posts.entity.PostCategory;
import com.sparta.team2project.posts.entity.PostsPictures;
import com.sparta.team2project.posts.dto.*;
import com.sparta.team2project.posts.entity.Posts;
import com.sparta.team2project.posts.repository.PostsPicturesRepository;
import com.sparta.team2project.posts.repository.PostsRepository;
import com.sparta.team2project.postslike.entity.PostsLike;
import com.sparta.team2project.postslike.repository.PostsLikeRepository;
import com.sparta.team2project.s3.AmazonS3ResourceStorage;
import com.sparta.team2project.s3.CustomMultipartFile;
import com.sparta.team2project.tags.entity.Tags;
import com.sparta.team2project.tags.repository.TagsRepository;
import com.sparta.team2project.tripdate.entity.TripDate;
import com.sparta.team2project.tripdate.repository.TripDateRepository;
import com.sparta.team2project.users.UserRepository;
import com.sparta.team2project.users.Users;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PostsService {
    private final PostsRepository postsRepository;
    private final TripDateRepository tripDateRepository;
    private final PostsLikeRepository postsLikeRepository;
    private final UserRepository usersRepository;
    private final CommentsRepository commentsRepository;
    private final TagsRepository tagsRepository;

    // 사진 저장을 위한 필드 선언
    private final AmazonS3ResourceStorage amazonS3ResourceStorage;
    private final AmazonS3Client amazonS3Client;
    private final PostsPicturesRepository postsPicturesRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 게시글 생성
    public PostMessageResponseDto createPost(TotalRequestDto totalRequestDto,Users users) {

        Users existUser = checkUser(users); // 사용자 조회

        PostCategory postCategory = checkCategory(totalRequestDto.getPostCategory()); // 카테고리 null check

        Posts posts = new Posts(totalRequestDto.getContents(),
                totalRequestDto.getTitle(),
                postCategory,
                totalRequestDto.getSubTitle(),
                existUser);
        postsRepository.save(posts);  //posts 저장

        List<String> tagsList = totalRequestDto.getTagsList();
        tagsList.stream()
                .map(tag -> new Tags(tag, posts))
                .forEach(tagsRepository::save); // tags 저장


        List<Long> idList = new ArrayList<>();// tripDateID 담는 리스트
        List<TripDateOnlyRequestDto> tripDateRequestDtoList = totalRequestDto.getTripDateList();
        if(totalRequestDto.getTripDateList() != null && !totalRequestDto.getTripDateList().isEmpty()){
            for(TripDateOnlyRequestDto tripDateRequestDto : tripDateRequestDtoList){
                TripDate tripDate = new TripDate(tripDateRequestDto,posts);
                tripDateRepository.save(tripDate); // tripDate 저장
                idList.add(tripDate.getId());
            }
            return new PostMessageResponseDto("게시글이 등록 되었습니다.", HttpServletResponse.SC_OK,posts,idList);
        } else {return  new PostMessageResponseDto("게시글이 등록 되었습니다.", HttpServletResponse.SC_OK,posts);}
    }

    // 단일 게시물 조회
    public PostResponseDto getPost(Long postId) {

        Posts posts = checkPosts(postId); // 게시물 id 조회

        posts.viewCount();// 조회수 증가 시키는 메서드
        int commentNum = commentsRepository.countByPosts(posts); // 댓글 세는 메서드

        List<Tags> tags = tagsRepository.findByPosts(posts); // 해당 게시물 관련 태그 조회

        return new PostResponseDto(posts, posts.getUsers(), tags, commentNum, posts.getModifiedAt());
    }

    // 게시글 전체 조회
    @Transactional(readOnly = true)
    public Slice<PostResponseDto> getAllPosts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Posts> postsPage = postsRepository.findAllPosts(pageable);

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Posts posts : postsPage) {
            int commentNum = commentsRepository.countByPosts(posts); // 댓글 세는 메서드
            List<Tags> tag = tagsRepository.findByPosts(posts);
            postResponseDtoList.add(new PostResponseDto(posts, posts.getUsers(), tag, commentNum));
        }
        return new SliceImpl<>(postResponseDtoList, pageable, postsPage.hasNext());
    }

    // 사용자별 게시글 전체 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getUserPosts(Users users) {

        Users existUser = checkUser(users); // 사용자 조회
        List<Posts> postsList = postsRepository.findByUsersOrderByCreatedAtDesc(existUser);

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();

        for (Posts posts : postsList) {

            int commentNum = commentsRepository.countByPosts(posts); // 댓글 세는 메서드

            List<Tags> tags = tagsRepository.findByPosts(posts);
            List<TripDate> tripDateList = tripDateRepository.findByPosts(posts);

            postResponseDtoList.add(new PostResponseDto(posts, tags, posts.getUsers(), commentNum, tripDateList));
        }
        return postResponseDtoList;
    }

    // 키워드 검색
    @Transactional(readOnly = true)
    public List<PostResponseDto> getKeywordPosts(String keyword) {

        if (keyword == null || keyword.isEmpty()) { // 키워드가 null값인 경우
            throw new CustomException(ErrorCode.POST_NOT_SEARCH);
        }

        // 중복을 방지하기 위한 Set 사용
        Set<Posts> postsSet = postsRepository.searchKeyword(keyword);


        if (postsSet.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_EXIST);
        }

        List<Posts> postsList = new ArrayList<>(postsSet); //Set-> List로 바꿔줌

        // createdAtAt 기준으로 내림차순 정렬
        postsList.sort(Comparator.comparing(Posts::getCreatedAt).reversed());

        return getPostResponseDto(postsList);
    }

    // 랭킹 목록 조회(상위 10개)
    @Transactional(readOnly = true)
    public List<PostResponseDto> getRankPosts() {

        // 상위 10개 게시물 가져오기 (좋아요 수 겹칠 시 createdAt 내림차순으로 정렬)
        List<Posts> postsList = postsRepository.findTop10ByTitleIsNotNullAndContentsIsNotNullOrderByLikeNumDescCreatedAtDesc();
        return getPostResponseDto(postsList);
    }

    // 사용자가 좋아요 누른 게시물 조회
    @Transactional(readOnly = true)
    public Slice<PostResponseDto> getUserLikePosts(Users users, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Users existUser = checkUser(users); // 사용자 조회
        Page<Posts> postsPage = postsRepository.findUsersLikePosts(existUser, pageable);

        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Posts posts : postsPage) {
            postResponseDtoList.add(new PostResponseDto(posts, posts.getUsers()));
        }
        return new SliceImpl<>(postResponseDtoList, pageable, postsPage.hasNext());
    }

    // 사용자가 좋아요 누른 게시물 id만 조회
    @Transactional(readOnly = true)
    public List<Long> getUserLikePostsId(Users users) {

        Users existUser = checkUser(users); // 사용자 조회
        List<Long> idList = postsRepository.findUsersLikePostsId(existUser);

        return idList;
    }

    // 게시글 좋아요 및 좋아요 취소
    public LikeResponseDto like(Long id, Users users) {
        Posts posts = checkPosts(id); // 게시글 조회

        Users existUser = checkUser(users); // 사용자 조회

        PostsLike overlap = postsLikeRepository.findByPostsAndUsers(posts, existUser);
        if (overlap != null) {
            postsLikeRepository.delete(overlap); // 좋아요 삭제
            posts.unlike(); // 해당 게시물 좋아요 취소시키는 메서드
            return new LikeResponseDto("좋아요 취소", HttpServletResponse.SC_OK, false);
        } else {
            PostsLike postsLike = new PostsLike(posts, existUser);
            postsLikeRepository.save(postsLike); // 좋아요 저장
            posts.like(); // 해당 게시물 좋아요수 증가시키는 메서드
            return new LikeResponseDto("좋아요 확인", HttpServletResponse.SC_OK, true);
        }
    }

    // 게시글 수정
    public MessageResponseDto updatePost(Long postId, UpdateRequestDto updateRequestDto, Users users) {
        Posts posts = checkPosts(postId); // 게시글 조회
        Users existUser = checkUser(users); // 사용자 조회
        checkAuthority(existUser, posts.getUsers()); //권한 확인 (ROLE 확인 및 게시글 사용자 id와 토큰에서 가져온 사용자 id 일치 여부 확인)

        if (posts.getContents() != null && posts.getTitle() != null) { // 이미 게시글 등록이 완료 된 경우

            posts.update(updateRequestDto); // 게시글 수정
        } else {
            LocalDateTime time = LocalDateTime.now(); // 게시글 등록할 때의 시간
            posts.updateTime(updateRequestDto, time); // null 값인 부분 채워줌으로써 게시글 등록
        }
        List<Tags> tagList = tagsRepository.findByPosts(posts); // 기존 게시물 태그 삭제
        tagsRepository.deleteAll(tagList);

        List<String> tagsList = updateRequestDto.getTagsList();
        tagsList.stream()
                .map(tag -> new Tags(tag, posts))
                .forEach(tagsRepository::save); // 수정된 tags 저장
        return new MessageResponseDto("수정 되었습니다.", HttpServletResponse.SC_OK);
    }

    // 해당 게시물 삭제
    public MessageResponseDto deletePost(Long postId, Users users) {
        Posts posts = checkPosts(postId); // 게시글 조회
        Users existUser = checkUser(users); // 사용자 조회
        checkAuthority(existUser, posts.getUsers()); //권한 확인(ROLE 확인 및 게시글 사용자 id와 토큰에서 가져온 사용자 id 일치 여부 확인)

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
        return new MessageResponseDto("삭제 되었습니다.", HttpServletResponse.SC_OK);
    }

    // 사용자 조회 메서드
    private Users checkUser(Users users) {
        return usersRepository.findByEmail(users.getEmail()).
                orElseThrow(() -> new CustomException(ErrorCode.ID_NOT_MATCH));

    }

    // ADMIN 권한 및 이메일 일치여부 메서드
    public void checkAuthority(Users existUser, Users users) {
        if (!existUser.getUserRole().equals(UserRoleEnum.ADMIN) && !existUser.getEmail().equals(users.getEmail())) {
            throw new CustomException(ErrorCode.NOT_ALLOWED);
        }
    }

    // 카테고리 null 체크
    private PostCategory checkCategory(PostCategory postCategory) {
        if (postCategory == null) {
            throw new CustomException(ErrorCode.CATEGORY_IS_BLANK);
        } else {
            return postCategory;
        }
    }


    // 게시글 조회 메서드
    private Posts checkPosts(Long id) {
        return postsRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_EXIST));
    }

    // 상위 랭킹 및 검색 조회한 게시글 관련 반환 시 사용 메서드
    private List<PostResponseDto> getPostResponseDto(List<Posts> postsList) {
        if (postsList.isEmpty()) {
            throw new CustomException(ErrorCode.POST_NOT_EXIST);
        }
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for (Posts posts : postsList) {
            int commentNum = commentsRepository.countByPosts(posts); // 댓글 세는 메서드
            List<Tags> tag = tagsRepository.findByPosts(posts);
            postResponseDtoList.add(new PostResponseDto(posts, tag, posts.getUsers(), commentNum));
        }
        return postResponseDtoList;
    }

    // 사진 업로드 사이즈 조정 메서드
    @Transactional
    public MultipartFile resizer(String fileName, String fileFormat, MultipartFile originalImage, int height) {

        try {
            BufferedImage image = ImageIO.read(originalImage.getInputStream());// MultipartFile -> BufferedImage Convert

            int originWidth = image.getWidth();
            int originHeight = image.getHeight();

            // origin 이미지가 400보다 작으면 패스
            if (originHeight < height)
                return originalImage;

            MarvinImage imageMarvin = new MarvinImage(image);

            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", height * originWidth / originHeight); //비율유지를 위해 너비를 비율로 계산
            scale.setAttribute("newHeight", height);
            scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

            BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imageNoAlpha, fileFormat, baos);
            baos.flush();

            return new CustomMultipartFile(fileName, fileFormat, originalImage.getContentType(), baos.toByteArray());

        } catch (IOException e) {
            throw new CustomException(ErrorCode.UNABLE_TO_CONVERT);
        }
    }

    // 게시글 사진 업로드 API
    @SneakyThrows
    public String uploadPostsPictures(Long postId, MultipartFile file, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users);         // 권한 확인
        // 기 존재하는 사진이 있는지 확인
        Posts checkPosts = postsRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
        );
        List<PostsPictures> checkPostsPicturesList = checkPosts.getPostsPicturesList();
        // 기 존재하는 사진 모음이 3개인지 확인
        if (checkPostsPicturesList.size() == 3) {
            throw new CustomException(ErrorCode.EXCEED_PICTURES_LIMIT);
        }
        // 이미 입력된 사진 + 새로 입력할 사진이 3개를 초과하면 예외처리
        else {
            // 1. 파일 정보를 picturesResponseDtoList에 저장
            // 해당 위치의 파일 이름이 null값이면 사진 등록 작업 수행
            String postsPicturesName = file.getOriginalFilename();
            String postsPicturesURL = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com" + "/" + "postsPictures" + "/" + postsPicturesName;
            String postsPictureContentType = file.getContentType();
            String fileFormatName = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
            // 2. 이미지 리사이즈 함수 호출
//            MultipartFile resizedImage = resizer(postsPicturesName, fileFormatName, file, 300);
            Long postsPictureSize = file.getSize();  // 단위: KBytes
            // 3. Repository에 파일 정보를 저장하기 위해 PicturesList에 저장(schedulesId 필요)
            Posts posts = postsRepository.findById(postId).orElseThrow(
                    () -> new CustomException(ErrorCode.ID_NOT_MATCH)
            );
            // 4. 기 존재하는 PostsPictures의 null값들을 업데이트
            PostsPictures postsPictures = new PostsPictures(posts, postsPicturesURL, postsPicturesName, postsPictureContentType, postsPictureSize);
            checkPostsPicturesList.add(postsPictures);
            // 4. 사진을 메타데이터 및 정보와 함께 S3에 저장
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(postsPictureContentType);
            metadata.setContentLength(postsPictureSize);
            try (InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket + "/postsPictures", postsPicturesName, inputStream, metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new CustomException(ErrorCode.S3_NOT_UPLOAD);
            }

            // 4. Repository에 Pictures리스트를 저장
            postsPicturesRepository.save(postsPictures);// 5. 성공 메시지 DTO와 함께 picturesResponseDtoList를 반환
            return postsPicturesURL;
        }
    }

    public PostsPicturesUploadResponseDto getPostsPictures(Long postId) {
        // 1. Schedules 객체를 찾아 연결된 Pictures 불러오기
        Posts posts = postsRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
        );
        // 2. 불러온 Pictures의 리스트를 DTO의 리스트로 변환
        List<PostsPictures> postsPicturesList = posts.getPostsPicturesList();
        List<PostsPicturesResponseDto> postsPicturesResponseDtoList = new ArrayList<>(3);
        for (PostsPictures postsPictures : postsPicturesList) {
            // 3. 파일 불러오기
            try {
                S3Object s3Object = amazonS3Client.getObject(bucket + "/postsPictures", postsPictures.getPostsPicturesName());
                S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
                FileOutputStream fileOutputStream = new FileOutputStream(new File(postsPictures.getPostsPicturesName()));
                byte[] read_buf = new byte[1024];
                int read_len = 0;
                while ((read_len = s3ObjectInputStream.read(read_buf)) > 0) {
                    fileOutputStream.write(read_buf, 0, read_len);
                }
                s3ObjectInputStream.close();
                fileOutputStream.close();
            } catch (AmazonServiceException e) {
                throw new AmazonServiceException(e.getErrorMessage());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e.getMessage());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            // 4. 각 사진 파일 정보(Pictures)를 DTO리스트에 저장
            PostsPicturesResponseDto postsPicturesResponseDto = new PostsPicturesResponseDto(postsPictures);
            postsPicturesResponseDtoList.add(postsPicturesResponseDto);
        }
        // 5. 성공 메시지와 함께 사진 정보 반환
        MessageResponseDto messageResponseDto = new MessageResponseDto("요청한 파일을 반환하였습니다.", 200);
        PostsPicturesUploadResponseDto postsPicturesUploadResponseDto = new PostsPicturesUploadResponseDto(postsPicturesResponseDtoList, messageResponseDto);
        return postsPicturesUploadResponseDto;
    }

    public String getPostsPicture(Long postsPicturesId) {
        try {
            // 1. 파일을 찾아 열기
            PostsPictures postsPictures = postsPicturesRepository.findById(postsPicturesId).orElseThrow(
                    () -> new CustomException(ErrorCode.ID_NOT_MATCH)
            );
            S3Object s3Object = amazonS3Client.getObject(bucket + "/postsPictures", postsPictures.getPostsPicturesName());
            S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(postsPictures.getPostsPicturesName()));
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3ObjectInputStream.read(read_buf)) > 0) {
                fileOutputStream.write(read_buf, 0, read_len);
            }
            s3ObjectInputStream.close();
            fileOutputStream.close();
            // 2. 사진 파일 정보(Pictures) 반환
            return postsPictures.getPostsPicturesURL();
        } catch (AmazonServiceException e) {
            throw new AmazonServiceException(e.getErrorMessage());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String updatePictures(Long postsPicturesId, MultipartFile file, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users);         // 권한 확인
        PostsPictures postsPictures = postsPicturesRepository.findById(postsPicturesId).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_MATCH));
        // 1. 파일 기본 정보 추출
        String postsPicturesName = file.getOriginalFilename();
        String postsPicturesURL = "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com" + "/" + "postsPictures" + "/" + postsPicturesName;
        String postsPictureContentType = file.getContentType();
        String fileFormatName = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
        // 2. 이미지 사이즈 재조정
//        MultipartFile resizedImage = resizer(postsPicturesName, fileFormatName, file, 250);
        Long postsPictureSize = file.getSize();  // 단위: KBytes
        postsPictures.updatePostsPictures(postsPicturesURL, postsPicturesName, postsPictureContentType, postsPictureSize);
        postsPicturesRepository.save(postsPictures);
        // 3. 사진을 메타데이터 및 정보와 함께 S3에 저장
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(postsPictureContentType);
        metadata.setContentLength(postsPictureSize);
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket + "/postsPictures", postsPicturesName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_NOT_UPLOAD);
        }
//        MessageResponseDto messageResponseDto = new MessageResponseDto("사진이 업데이트 되었습니다.", 200);
//        PostsPicturesMessageResponseDto postsPicturesMessageResponseDto = new PostsPicturesMessageResponseDto(postsPicturesResponseDto, messageResponseDto);
        return postsPicturesURL;
    }

    public MessageResponseDto deletePictures(Long postsPicturesId, Users users) {
        Users existUser = checkUser(users); // 유저 확인
        checkAuthority(existUser, users);         // 권한 확인
        PostsPictures postsPictures = postsPicturesRepository.findById(postsPicturesId).orElseThrow(
                () -> new CustomException(ErrorCode.ID_NOT_MATCH)
        );
        try {
            amazonS3Client.deleteObject(bucket + "/postsPictures", postsPictures.getPostsPicturesName());
        } catch (AmazonServiceException e) {
            throw new AmazonServiceException(e.getErrorMessage());
        }
        postsPicturesRepository.delete(postsPictures);
        MessageResponseDto messageResponseDto = new MessageResponseDto("사진이 삭제되었습니다.", 200);
        return messageResponseDto;
    }


}
