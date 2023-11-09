package com.sparta.team2project.chatting.repository;


import com.sparta.team2project.chatting.dto.ChatRoomDto;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ChatRoomRepository {

    private Map<String, ChatRoomDto> chatRoomDtoMap;

    // 의존하는 객체를 설정한 이후 초기화 작업을 수행하는 메서드에 적용
    @PostConstruct
    private void init() {
        chatRoomDtoMap = new LinkedHashMap<>();
    }

    public List<ChatRoomDto> findAllRoom(){
        // 채팅방 생성순서 최근 순으로 반환
        List chatRooms = new ArrayList<>(chatRoomDtoMap.values());
        Collections.reverse(chatRooms);
        return chatRooms;
    }

    public ChatRoomDto findRoomById(String id) {
        return chatRoomDtoMap.get(id);
    }

    public ChatRoomDto createChatRoom(String name) {
        ChatRoomDto chatRoomDto = ChatRoomDto.create(name);
        chatRoomDtoMap.put(chatRoomDto.getRoomId(), chatRoomDto);
        return chatRoomDto;
    }
}
