package com.example.demo.src.post.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Post {
    private int postId;
    private int userId;
    private String title;
    private int categoryId;
    private String productName;
    private int price;
    private int locationId;
    private Timestamp date;
    private int num;
    private String content;
    private String transactionStatus;
    private int status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
