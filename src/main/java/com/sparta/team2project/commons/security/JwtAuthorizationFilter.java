package com.sparta.team2project.commons.security;

import com.sparta.team2project.commons.Util.JwtUtil;
import com.sparta.team2project.commons.Util.RedisUtil;
import com.sparta.team2project.commons.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
//@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisUtil redisUtil;


    // 헤더에 담아서 요청할 때
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 Access Token과 Refresh Token 추출
        String accessToken = jwtUtil.getJwtFromHeader(req, jwtUtil.ACCESS_KEY);
        String refreshToken = jwtUtil.getJwtFromHeader(req, jwtUtil.REFRESH_KEY);

        if (StringUtils.hasText(accessToken)) {
            try {
                if (!jwtUtil.validateToken(accessToken)) {
                    if (jwtUtil.validateToken(refreshToken) && redisUtil.exists(refreshToken)) {
                        log.info("AccessToken가 만료되어 RefreshToken으로 새로운 AccessToken을 발급합니다.");
                        Claims refreshTokenInfo = jwtUtil.getUserInfoFromToken(refreshToken);
                        String email = refreshTokenInfo.getSubject();
                        UserRoleEnum userRole = UserRoleEnum.valueOf(refreshTokenInfo.get("userRole", String.class));

                        // 새로운 AccessToken 생성
                        String newAccessToken = jwtUtil.createAccessToken(email, userRole);
                        res.addHeader(jwtUtil.ACCESS_KEY, newAccessToken);
                    } else {
                        // Refresh Token도 만료되었거나 유효하지 않을 경우, 인증 실패 처리
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        log.info("RefreshToken이 만료되었습니다.");
                    }
                } else {
                    Claims accessTokenInfo = jwtUtil.getUserInfoFromToken(accessToken);

                    log.info("Token 권한 확인");
                    setAuthentication(accessTokenInfo.getSubject());
                }
            } catch (JwtException e) {
                // JWT 토큰 유효성 검사 실패 시 예외 처리
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                log.error(e.getMessage());
            }
        }

        filterChain.doFilter(req, res);
    }

    // 인증 처리
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}