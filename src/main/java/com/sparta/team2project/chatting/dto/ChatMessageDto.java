package com.sparta.team2project.chatting.dto;

import lombok.Getter;

@Getter

public class ChatMessageDto {
    // 메세지 타입 : 입장, 채팅
    public enum MessageType{
        ENTER,

        TALK
    }

    private MessageType type; // 메세지 타입
    private String roomId; // 방번호
    private String sender; //메세지 보낸사람
    private String message; // 메세지

    public void setMessage(String message) {
        this.message = message;
    }
}
