package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCommentList {
    private int commentId;
    private int userId;
    private int parentId;
    private String nick;
    private String comment;
    private String createdAt;
}