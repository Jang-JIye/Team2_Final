package com.sparta.team2project.users;

import com.sparta.team2project.comments.entity.Comments;
import com.sparta.team2project.comments.repository.CommentsRepository;
import com.sparta.team2project.commons.Util.RedisUtil;
import com.sparta.team2project.commons.dto.MessageResponseDto;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.commons.exceptionhandler.CustomException;
import com.sparta.team2project.commons.exceptionhandler.ErrorCode;
import com.sparta.team2project.commons.Util.JwtUtil;
import com.sparta.team2project.email.EmailService;
import com.sparta.team2project.email.dto.ValidNumberRequestDto;
import com.sparta.team2project.posts.dto.PostResponseDto;
import com.sparta.team2project.posts.entity.Posts;
import com.sparta.team2project.posts.repository.PostsRepository;
import com.sparta.team2project.profile.Profile;
import com.sparta.team2project.profile.ProfileRepository;
import com.sparta.team2project.tags.entity.Tags;
import com.sparta.team2project.tags.repository.TagsRepository;
import com.sparta.team2project.tripdate.entity.TripDate;
import com.sparta.team2project.tripdate.repository.TripDateRepository;
import com.sparta.team2project.users.dto.LoginRequestDto;
import com.sparta.team2project.users.dto.SignoutRequestDto;
import com.sparta.team2project.users.dto.SignupRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;
    private final PostsRepository postsRepository;
    private final TagsRepository tagsRepository;
    private final TripDateRepository tripDateRepository;
    private final CommentsRepository commentsRepository;

    private final EmailService emailService;
    private final RedisUtil redisUtil;

    private final JwtUtil jwtUtil;


    // ADMIN_TOKEN
    @Value("${ADMIN_TOKEN}")
    private String ADMIN_TOKEN;

    public ResponseEntity<MessageResponseDto> signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<Users> checkUserId = userRepository.findByEmail(email);
        if (checkUserId.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
        // 사용자 ROLE 확인
        UserRoleEnum userRole = UserRoleEnum.USER;

        if (requestDto.getAdminToken() != null && requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
            userRole = UserRoleEnum.ADMIN;
        }
        // 닉네임이 이미 있을 경우 예외처리
        if (userRepository.existsByNickName(requestDto.getNickName())) {
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }
        // 기본값 설정
        String nickName = createRandomNickName();
        String profileImg = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fb0SLv8%2FbtsyLoUxvAs%2FSKsGiOc7TzkebNvH4ZQE9K%2Fimg.png";
        // 입력값이 존재한다면 기본값 대체
        if (requestDto.getNickName() != null) {
            nickName = requestDto.getNickName();
        }
        if (requestDto.getProfileImg() != null) {
            profileImg = requestDto.getProfileImg();
        }

        // 사용자 등록
        Users users = new Users(email, nickName, password, userRole, profileImg);
        userRepository.save(users);
        // 프로필 생성
        Profile profile = new Profile(users);
        profileRepository.save(profile);

        return ResponseEntity.ok(new MessageResponseDto("회원가입 완료", HttpStatus.CREATED.value()));
    }


    // 인증번호 요청
    public ResponseEntity<MessageResponseDto> checkEmail(String email) {
        // 이메일 관련 검증 및 인증번호 생성 코드는 그대로 사용
        int number = (int) (Math.random() * 899999) + 100000; // 6자리 난수 생성(인증번호)
        // Redis를 사용하여 인증번호 저장
        redisUtil.setDataExpire(email, String.valueOf(number), 180000);

        emailService.sendNumber(number, email);
        return ResponseEntity.ok((new MessageResponseDto("인증번호가 발송되었습니다.", HttpStatus.OK.value())));
    }

    // 인증 번호 확인하기
    public boolean checkValidNumber(ValidNumberRequestDto validNumberRequestDto, String email) {
        // 이메일 관련 검증 코드는 그대로 사용

        // Redis에서 인증번호 가져오기
        String storedNumber = redisUtil.getData(email);

        if (storedNumber == null || !storedNumber.equals(String.valueOf(validNumberRequestDto.getValidNumber()))) {
            throw new CustomException(ErrorCode.WRONG_NUMBER);
        } else {
            return true; // 인증 성공시 true 값이 반환됩니다.
        }
    }

    // 닉네임 중복여부 체크 메서드
    public boolean checkNickName(String nickName) {
        boolean checkNickName = true;
        if (userRepository.existsByNickName(nickName)) {
            throw new CustomException(ErrorCode.DUPLICATED_NICKNAME);
        }
        return checkNickName; // 중복이 아닐 경우 true 값이 반환됩니다.
    }


    // 회원탈퇴
    public ResponseEntity<MessageResponseDto> deleteUser(SignoutRequestDto requestDto, String email) {
        Users users = userRepository.findByEmail(email).orElseThrow(() ->
                new CustomException(ErrorCode.ID_NOT_FOUND)); //사용자가 존재하지 않음
        if (!passwordEncoder.matches(requestDto.getPassword(), users.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH); // 해당 이메일의 비밀번호가 일치하지 않음
        }
        if (!requestDto.getEmail().equals(email)) {
            throw new CustomException(ErrorCode.ID_NOT_MATCH); // 로그인한 이메일과 삭제하려는 이메일이 일치하지 않음
        }
        Profile userProfile = profileRepository.findByUsers_Email(email).orElseThrow(() ->
                new CustomException(ErrorCode.EMAIL_NOT_FOUND)
        );
        if (userProfile != null) {
            profileRepository.delete(userProfile);
        }
        List<Posts> userPosts = postsRepository.findByUsers(users);
        if (userPosts != null) {
            for (Posts posts : userPosts) {
                List<Comments> relatedComments = commentsRepository.findByPosts(posts);
                commentsRepository.deleteAll(relatedComments);
                List<TripDate> relatedTripDates = tripDateRepository.findByPosts(posts);
                tripDateRepository.deleteAll(relatedTripDates);
                List<Tags> relatedTags = tagsRepository.findByPosts(posts);
                tagsRepository.deleteAll(relatedTags);
                postsRepository.delete(posts);
            }
        }

        userRepository.delete(users);
        // 리프레시 토큰 삭제
        redisUtil.deleteRefreshToken(email);
        return ResponseEntity.ok(new MessageResponseDto("회원탈퇴 완료", HttpStatus.OK.value()));
    }

    // 로그인 기능
    public ResponseEntity<MessageResponseDto> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Users users = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(() ->
                new CustomException(ErrorCode.ID_NOT_FOUND)); // 이메일 여부 확인
        if (!passwordEncoder.matches(requestDto.getPassword(), users.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH); // 해당 이메일의 비밀번호가 일치하지 않음
        }
        // 로그인 성공 시 액세스 토큰 생성
        String accessToken = jwtUtil.createAccessToken(users.getEmail(), users.getUserRole()); // 수정: userRole 대신 userRole 사용하도록 변경
        // Redis에 저장된 리프레시 토큰을 가져옴
        String storedRefreshToken = redisUtil.getRefreshToken(users.getEmail());
        if (storedRefreshToken == null) {
            // 저장된 리프레시 토큰이 없으면 새로 생성
            String refreshToken = jwtUtil.createRefreshToken(users.getEmail(), users.getUserRole());
            // Redis에 새로운 리프레시 토큰 저장
            redisUtil.saveRefreshToken(users.getEmail(), refreshToken);
            response.addHeader(JwtUtil.REFRESH_KEY, refreshToken);
        } else {
            // 이미 저장된 리프레시 토큰 사용
            response.addHeader(JwtUtil.REFRESH_KEY, storedRefreshToken);
        }
        response.addHeader(JwtUtil.ACCESS_KEY, accessToken);

        return ResponseEntity.ok(new MessageResponseDto("로그인 성공", HttpStatus.OK.value()));
    }

    //로그아웃 기능
    public ResponseEntity<MessageResponseDto> logout(String email, HttpServletResponse response) {
        // 리프레시 토큰 삭제
        redisUtil.deleteRefreshToken(email);

        return ResponseEntity.ok(new MessageResponseDto("로그아웃 완료", HttpStatus.OK.value()));
    }

    // 랜덤 닉네임 생성 메서드
    public String createRandomNickName() {
        String[] nickName =
                {"행복한", "즐거운", "평화로운", "요망진", "귀여운", "화가난", "달리는", "잠자는", "놀고있는", "하늘을나는", "여행가고싶은", "영앤리치", "목적지가없는", "바쁘다바빠!", "하고싶은말이많은", "어른스러운", "깜찍한", "엉뚱한", "소심한", "화이팅!", "두둠칫", "힘내랏"};
        String[] nickName2 =
                {"여행자", "뚜벅이", "배낭꾼", "백엔드개발자", "프론트엔드개발자", "디자이너", "캠핑족", "맛집탐방러", "힐링족", "프로그래머", "자유여행자", "휴가족", "현대사회", "예술가", "돌하루방", "편", "탈출러", "풀스택개발자", "하루와하트"};

        int maxCreateRandomNickName = nickName.length * nickName2.length; // 경우의 수
        int maxTries = 300;
        for (int tries = 0; tries < maxTries; tries++) {
            int random = (int) (Math.random() * nickName.length);
            int random2 = (int) (Math.random() * nickName2.length);

            String randomNickName = nickName[random] + " " + nickName2[random2];

            if (!userRepository.existsByNickName(randomNickName)) {
                return randomNickName;
            }
        }
        throw new CustomException(ErrorCode.RANDOM_NICKNAME_FAIL);
    }

}
