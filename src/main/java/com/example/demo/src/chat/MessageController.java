package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.chat.model.ChatMessage;
import com.example.demo.src.chat.model.ChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;
import com.example.demo.utils.JwtService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {
//    @Autowired
//    private final ChatService chatService;
//    @Autowired
//    private final JwtService jwtService;
//
//
//    private final SimpMessageSendingOperations sendingOperations;
//
//    @MessageMapping("/chat/message")
//    public void enter(ChatMessage message) {
//
//        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
//            message.setMessage(message.getSender()+"님이 입장하였습니다.");
//            chatService.enterRoom(message.getRoomId(),message.getSender());
//        }else{
//            //메시지 저장
//            chatService.createMessage(message);
//        }
//        sendingOperations.convertAndSend("/sub/chat/room/"+message.getRoomId(),message);
//
//
//
//
//    }
}