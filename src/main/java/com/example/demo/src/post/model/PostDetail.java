package com.example.demo.src.post.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDetail {
    private int postId;
    private String profileImg;
    private String nick;
    private String town;
    private String category;
    private String title;
    private int price;
    private String date; //수정할수있음
    private int num;
    private String content;
    private String transactionStatus;
    private int interestStatus;
    private String createdAt;


}
