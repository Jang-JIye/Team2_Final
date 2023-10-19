package com.sparta.team2project.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    // Header, Content-Type 등을 설정하여 외부 API 호출
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}