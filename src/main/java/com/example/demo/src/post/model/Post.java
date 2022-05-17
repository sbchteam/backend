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

    public void setNullPost (Post post) {
        if (this.userId == 0)
            this.setUserId(post.userId);
        if (this.title == null)
            this.setTitle(post.getTitle());
        if (this.categoryId == 0)
            this.setCategoryId(post.getCategoryId());
        if (this.productName == null)
            this.setProductName(post.getProductName());
        if (this.price == 0)
            this.setPrice(post.getPrice());
        if (this.getLocationId() == 0)
            this.setLocationId(post.getLocationId());
        if (this.getDate() == null)
            this.setDate(post.getDate());
        if (this.getNum() == 0)
            this.setNum(post.getNum());
        if (this.getContent() == null)
            this.setContent(post.getContent());
    }
}
