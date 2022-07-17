package com.example.demo.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    private int roomId;
    private int userId;
    private String nick;
    private String profileImg;
    private String message;
    private Timestamp lastTime;


//    public static ChatRoom create(String name) {
//        ChatRoom room = new ChatRoom();
//        room.roomId = UUID.randomUUID().toString();
//        room.roomName = name;
//        return room;
//    }
}