package com.sparta.team2project.chatting.handler;//package com.sparta.coxld.handler;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sparta.coxld.dto.ChatMessageDto;
//import com.sparta.coxld.dto.ChatRoomDto;
//import com.sparta.coxld.service.ChatService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class WebSocketChatHandler extends TextWebSocketHandler {
//    private final ObjectMapper objectMapper;
//    private final ChatService chatService;
//
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        String payload = message.getPayload();
//        log.info("payload {}", payload);
////        TextMessage textMessage = new TextMessage("Welcome chatting sever~^^");
////        session.sendMessage(textMessage);
//        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);
//        ChatRoomDto chatRoomDto = chatService.findRoomById(chatMessageDto.getRoomId());
//        chatRoomDto.handleActions(session, chatMessageDto, chatService);
//    }
//}
//
