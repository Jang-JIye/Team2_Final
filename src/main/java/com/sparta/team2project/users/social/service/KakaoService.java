package com.sparta.team2project.users.social.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import com.sparta.team2project.commons.jwt.JwtUtil;
import com.sparta.team2project.refreshToken.RefreshToken;
import com.sparta.team2project.refreshToken.RefreshTokenRepository;
import com.sparta.team2project.users.UserRepository;
import com.sparta.team2project.users.Users;
import com.sparta.team2project.users.social.dto.KakaoUserInfoDto;
import com.sparta.team2project.refreshToken.TokenDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Slf4j(topic = "Kakao Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;


    @Value("${kakaoClientId}")
    private String kakaoClientId;
    @Value("${kakaoClientSecret}")
    private String kakaoClientSecret;
    @Value("${kakaoRedirectUri}")
    private String kakaoRedirectUri;

    public String[] kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {

        String[] tokenArray = getToken(code);
        // 1. "인가 코드"로 "액세스, refresh 토큰" 요청
        String kakaoAccessToken = tokenArray[0];
        String kakaoRefreshToken = tokenArray[1];
        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);
        // 3. 필요시에 회원가입
        Users kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);
        // 4. JWT 토큰 반환
        TokenDto tokenDto = jwtUtil.createAllToken(kakaoUser.getEmail(), kakaoUser.getUserRole());
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByEmail(kakaoUser.getEmail());

        if (refreshToken.isPresent()) { //기존 회원
            RefreshToken updateToken = refreshToken.get().updateToken(tokenDto.getRefreshToken().substring(7), kakaoAccessToken, kakaoRefreshToken);
            refreshTokenRepository.save(updateToken);
        } else { //새로운 회원
            RefreshToken saveToken = new RefreshToken(tokenDto.getRefreshToken().substring(7), kakaoUser.getEmail(), kakaoAccessToken, kakaoRefreshToken);
            refreshTokenRepository.save(saveToken);
        }

        String[] tokenArrayResult = new String[5];
        tokenArrayResult[0] = tokenDto.getAccessToken();
        tokenArrayResult[1] = tokenDto.getRefreshToken();
        tokenArrayResult[2] = kakaoUserInfo.getEmail();
        tokenArrayResult[3]	=  kakaoUser.getNickName();

        if (kakaoUser.getProfileImg() != null) {
            tokenArrayResult[4] = kakaoUser.getProfileImg();
        } else {
            tokenArrayResult[4] = null;
        }

        return tokenArrayResult;
    }

    private String[] getToken(String code) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("client_secret", kakaoClientSecret);
        body.add("redirect_uri", kakaoRedirectUri);
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String[] tokenArray = new String[2];
        tokenArray[0] = jsonNode.get("access_token").asText();
        tokenArray[1] = jsonNode.get("refresh_token").asText();

        return tokenArray;
    }

    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoUserInfoDto(id, nickname, email);
    }

    private Users registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        Users kakaoUser = userRepository.findByKakaoId(kakaoId).orElse(null);
        try{
            if (kakaoUser == null) {
                // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
                String kakaoEmail = kakaoUserInfo.getEmail();
                Users sameEmailUser = userRepository.findByEmail(kakaoEmail).orElse(null);
                if (sameEmailUser != null) {
                    kakaoUser = sameEmailUser;
                    // 기존 회원정보에 카카오 Id 추가
                    kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
                } else {
                    // 신규 회원가입
                    String password = UUID.randomUUID().toString();
                    String encodedPassword = passwordEncoder.encode(password);

                    // email: kakao email
                    String email = kakaoUserInfo.getEmail();

                    kakaoUser = new Users(kakaoUserInfo.getNickname(), encodedPassword, email, UserRoleEnum.USER, kakaoId);
                }
                userRepository.save(kakaoUser);
            }
        }catch(Exception e){
            throw new RuntimeException(e+"카카오 저장오류");
        }
        return kakaoUser;
    }
}
