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
    private int postId;
    private String roomImg;
    private String title;
    private int price;
    private int num;
    private String message;
    private Timestamp lastTime;

}