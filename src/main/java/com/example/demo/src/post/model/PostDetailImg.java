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
public class PostDetailImg {
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

    private List<String> imgUrls;

    public void setPostDetail(PostDetail postDetail){
        this.setPostId(postDetail.getPostId());
        this.setProfileImg(postDetail.getProfileImg());
        this.setNick(postDetail.getNick());
        this.setTown(postDetail.getTown());
        this.setCategory(postDetail.getCategory());
        this.setTitle(postDetail.getTitle());
        this.setPrice(postDetail.getPrice());
        this.setDate(postDetail.getDate());
        this.setNum(postDetail.getNum());
        this.setContent(postDetail.getContent());
        this.setTransactionStatus(postDetail.getTransactionStatus());
        this.setInterestStatus(postDetail.getInterestStatus());
        this.setCreatedAt(postDetail.getCreatedAt());
    }
}
