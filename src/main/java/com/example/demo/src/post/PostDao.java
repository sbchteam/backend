package com.example.demo.src.post;

import com.example.demo.src.post.model.PostList;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<PostList> getPosts(int userId) {
        String getPostsQuery =
                "";
        int getPostsParams = userId;
        return this.jdbcTemplate.query(getPostsQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("created_at")
                ),
                getPostsParams, getPostsParams
        );
    }
    public List<PostList> getPostsInterest(int userId) {
        String getPostsInterestQuery =
                "";
        int getPostsInterestParams = userId;
        return this.jdbcTemplate.query(getPostsInterestQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("created_at")
                ),
                getPostsInterestParams, getPostsInterestParams
        );
    }

    public List<PostList> getPostsOngoing(int userId) {
        String getPostsOngoingQuery =
                "";
        int getPostsOngoingParams = userId;
        return this.jdbcTemplate.query(getPostsOngoingQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("created_at")
                ),
                getPostsOngoingParams, getPostsOngoingParams
        );
    }
}
