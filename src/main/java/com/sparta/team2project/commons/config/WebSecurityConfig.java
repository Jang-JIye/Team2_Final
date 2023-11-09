package com.sparta.team2project.commons.config;


import com.sparta.team2project.commons.Util.JwtUtil;

import com.sparta.team2project.commons.Util.RedisUtil;
import com.sparta.team2project.commons.security.JwtAuthorizationFilter;
import com.sparta.team2project.commons.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity // Spring Security 를 설정할 클래스라고 정의
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisUtil redisUtil; // RedisUtil 주입


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, redisUtil);
    }

    //Cors
    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();


        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://gallae-fe.vercel.app", "https://test-gallae.vercel.app"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        //configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setExposedHeaders(Arrays.asList("*"));
        configuration.setMaxAge(1800L);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CORS 설정
        http.cors((cors) -> cors.configurationSource(configurationSource()));

        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                                .requestMatchers("/").permitAll() // 메인 페이지 요청 허가
                                .requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/token/refreshAccessToken").permitAll()
                                // 조회기능은 누구나 가능합니다!
//                                .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/search/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/posts/rank").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/posts/*/comments/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/posts/*/comments/*/replies/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/comments/*/replies/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/schedules/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/chat/**").permitAll()
                                .requestMatchers(HttpMethod.GET).permitAll()
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll() // OpenAPI UI에 대한 엑세스 권한 허용
                                .requestMatchers("https://kapi.kakao.com", "/v2/user/me", "/kakao/callback/**").permitAll() // OpenAPI UI에 대한 엑세스 권한 허용

                                .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
