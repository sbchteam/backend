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
public class ChatRoomDetail {
    private int postId;
    private int userId;
    private String nick;
    private String profileImg;
    private String message;
    private Timestamp createdAt;
}