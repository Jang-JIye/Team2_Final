package com.sparta.team2project.chatting.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChatRoomDto {
    private String roomId;
    private String name;

    public static ChatRoomDto create(String name) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.roomId = UUID.randomUUID().toString(); // UUID : 네트워크 상에서 고유성이 보장되는 id
        chatRoomDto.name = name;
        return chatRoomDto;
    }
}
