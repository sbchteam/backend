package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class PostComment {

    private int commentId;
    private int postId;
    private int parentId;
    private int userId;
    private String content;
    private int status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}