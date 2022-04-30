package com.example.demo.src.post.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class PostInterest {
    private int postId;
    private int userId;
    private int status;
}
