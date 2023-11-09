package com.sparta.team2project.chatting.service;//package com.sparta.coxld.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.coxld.dto.ChatRoomDto;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.io.IOException;
//import java.util.*;
//
//@Slf4j
//@RequiredArgsConstructor
//@Service
//public class ChatService {
//
//    private final ObjectMapper objectMapper;
//    // 서버에 생성된 모든 채팅방의 정보를 모아둠
//    private Map<String, ChatRoomDto> chatRooms;
//
//    // 의존하는 객체를 설정한 이후 초기화 작업을 수행하는 메서드에 적용
//    @PostConstruct
//    private void init() {
//        chatRooms = new LinkedHashMap<>();
//    }
//
//    // 채팅방 조회 : 채팅방 Map에 담긴 정보를 조회
//    public List<ChatRoomDto> findAllRoom() {
//        return new ArrayList<>(chatRooms.values());
//    }
//
//    public ChatRoomDto findRoomById(String roomId) {
//        return chatRooms.get(roomId);
//    }
//
//    // 채팅방 생성 : Randoum UUID로 구별 ID를 가진 채팅방 객체를 생성하고 채팅방 Map에 추가
//    public ChatRoomDto createRoom(String name) {
//        String randomId = UUID.randomUUID().toString(); //UUID : 네트워크 상에서 고유성이 보장되는 id
//        ChatRoomDto chatRoomDto = ChatRoomDto.builder().roomId(randomId).name(name).build();
//        chatRooms.put(randomId, chatRoomDto);
//        return chatRoomDto;
//    }
//
//    // 메세지 발송 : 지정한 Websocket 세션에 메시지를 발송
//    public <T> void sendMessage (WebSocketSession session, T message) {
//        try {
//            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//}
