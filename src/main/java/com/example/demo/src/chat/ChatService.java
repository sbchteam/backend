package com.example.demo.src.chat;

import com.example.demo.src.chat.model.ChatMessage;
import com.example.demo.src.chat.model.ChatRoom;
import com.example.demo.src.chat.model.ChatRoomDetail;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.utils.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor

public class ChatService {
    private Map<String, ChatRoom> chatRooms;
    private final ChatDao chatDao;
    private final JwtService jwtService;

    @PostConstruct
    //의존관게 주입완료되면 실행되는 코드
    private void init() {
        chatRooms = new LinkedHashMap<>();
    }

    //채팅방 모두 불러오기
    public List<ChatRoom> getRooms(int userId) {
        //채팅방 최근 생성 순으로 반환
        List<ChatRoom> chatRooms= chatDao.getRooms(userId);
        Collections.reverse(chatRooms);
        return chatRooms;
    }
//    // 특정 공구 채팅방 목록 반환 -주최자 필요
//    public List<ChatRoom> getPostRooms(int userId,int postId) {
//        //채팅방 최근 생성 순으로 반환
//        List<ChatRoom> chatRooms= chatDao.getPostRooms(userId,postId);
//        Collections.reverse(chatRooms);
//        return chatRooms;
//    }

    //채팅방 하나 불러오기
    public List<ChatRoomDetail> getRoom(int roomId) {
        //채팅방 최근 생성 순으로 반환
        List<ChatRoomDetail> room= chatDao.getRoom(roomId);
        return room;
    }

    //채팅방 생성
    public int createRoom(int postId, int userId) { //생성한 유저 - 주최자
        //ChatRoom chatRoom = ChatRoom.create(postId);
        int roomId=chatDao.createRoom(postId,userId);
        return roomId;
    }

    //메시지 저장
    public void createMessage(ChatMessage message) { //생성한 유저id=채팅하기 누른 유저id(무조건 참가자)
        chatDao.createMessage(message);
    }

    //채팅방 입장
    public void enterRoom(int roomId, int userId) {
        chatDao.enterRoom(roomId,userId);
    }
}