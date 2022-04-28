package com.example.demo.src.post.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class PostList {
    private int postId;
    private int userId;
    private String title;
    private String Category;
    private int price;
    private String transactionStatus;
    private int interestStatus;
    private int interestNum;
    private String CreatedAt;

}
