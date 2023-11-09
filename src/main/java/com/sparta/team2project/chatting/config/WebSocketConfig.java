package com.sparta.team2project.chatting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 웹소켓 서버를 사용한다는 설정
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 서버 -> 클라이언트로 발행하는 메세지에 대한 endpoint 설정 : 구독
        config.enableSimpleBroker("/sub"); // 발행자가 queue (1:1) topic (1:다)의 경로로 메세지를 주면 구독자들에게 전달
        // 클라이언트 -> 서버로 발행하는 메세지에 대한 endpoint 설정 : 구독에 대한 메세지
        config.setApplicationDestinationPrefixes("/pub"); // 메세지 앞에 app이 붙어있는 경로로 발신되면 해당 경로를 처리하고 있는 핸들러로 전달됨 @MessageMapping 어노테이션이 붙은 곳을 타겟으로 한다는 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹 소켓이 hanshake를 하기 위해 연결하는 endpoint
        registry.addEndpoint("/ws-stomp") // WebSocket 또는 SockJS가 웹소켓 핸드세이크 커넥션을 생성할 경로
                .setAllowedOriginPatterns("*")
                .withSockJS(); // WebSocket을 지원하지 않는 브라우저에서 HTTP의 Polling과 같은 방식으로 WebSocket의 요청을 수행하도록 도와줌
    }
}