package com.example.demo.src.user.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class UserPosts {
    private int postId;
    private int userId;
    private String category;
    private String title;
    private int price;
    private String createdAt;
    private String imgUrl;
}
