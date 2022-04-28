package com.example.demo.src.post.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Time;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class PostList {
    private int postId;
    private String title;
    private String category;
    private int price;
    private String transactionStatus;
    private int interestStatus;
    private int interestNum;
    private Timestamp createdAt;
    private String imgUrl;

}
