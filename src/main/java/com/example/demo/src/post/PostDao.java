package com.example.demo.src.post;

import com.example.demo.src.post.model.PostDetail;
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
                "select *\n" +
                "from(\n" +
                "select title, price, num,transaction_status,p.created_at,\n" +
                "       category,\n" +
                "       ifnull(interestNum,0) as interestNum,\n" +
                "       max( case\n" +
                "       when pi.user_id =? then pi.status\n" +
                "       when pi.user_id!=? then 0\n" +
                "       when pi.user_id is null then 0\n" +
                "       end ) as interestStatus,\n" +
                "       img\n" +
                "from post p\n" +
                "left join post_interest pi\n" +
                "on p.id = pi.post_id\n" +
                "join category c\n" +
                "on c.id = p.category_id\n" +
                "left join post_image pimg\n" +
                "on p.id = pimg.post_id\n" +
                "left join (\n" +
                "    select post_id, count(*) as interestNum\n" +
                "    from post_interest\n" +
                "    group by post_id\n" +
                ") incnt on p.id=incnt.post_id\n" +
                "group by p.id) plist \n" +
                "order by plist.created_at desc;";
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
                "select *\n" +
                "from(\n" +
                "select title, price, num,transaction_status,p.created_at,\n" +
                "       category,\n" +
                "       ifnull(interestNum,0) as interestNum,\n" +
                "       max( case\n" +
                "       when pi.user_id =? then pi.status\n" +
                "       when pi.user_id!=? then 0\n" +
                "       when pi.user_id is null then 0\n" +
                "       end ) as interestStatus,\n" +
                "       img\n" +
                "from post p\n" +
                "left join post_interest pi\n" +
                "on p.id = pi.post_id\n" +
                "join category c\n" +
                "on c.id = p.category_id\n" +
                "left join post_image pimg\n" +
                "on p.id = pimg.post_id\n" +
                "left join (\n" +
                "    select post_id, count(*) as interestNum\n" +
                "    from post_interest\n" +
                "    group by post_id\n" +
                ") incnt on p.id=incnt.post_id\n" +
                "group by p.id) plist \n" +
                "order by plist.interestNum desc;";
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
                "select *\n" +
                "from(\n" +
                "select title, price, num,transaction_status,p.created_at,\n" +
                "       category,\n" +
                "       ifnull(interestNum,0) as interestNum,\n" +
                "       max( case\n" +
                "       when pi.user_id =? then pi.status\n" +
                "       when pi.user_id!=? then 0\n" +
                "       when pi.user_id is null then 0\n" +
                "       end ) as interestStatus,\n" +
                "       img\n" +
                "from post p\n" +
                "left join post_interest pi\n" +
                "on p.id = pi.post_id\n" +
                "join category c\n" +
                "on c.id = p.category_id\n" +
                "left join post_image pimg\n" +
                "on p.id = pimg.post_id\n" +
                "left join (\n" +
                "    select post_id, count(*) as interestNum\n" +
                "    from post_interest\n" +
                "    group by post_id\n" +
                ") incnt on p.id=incnt.post_id\n" +
                "where p.transaction_status\n" +
                "not in ('complete')\n" +
                "group by p.id) plist\n" +
                "order by plist.created_at desc;";
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

    //date처리, createdAt처리리
   public PostDetail getPost(int postId, int userId) {
        String getWorkQuery =
                "select p.id,u.profile_img,u.nick,town, category,title,price,date,num,content,transaction_status,ifnull(pi.status,0) as interestStatus,p.created_at\n" +
                "from post p\n" +
                "join user u\n" +
                "on p.user_id = u.id\n" +
                "join user_location ul\n" +
                "on p.location_id = ul.id\n" +
                "join category c\n" +
                "on p.category_id = c.id\n" +
                "left join post_interest pi\n" +
                "on p.id = pi.post_id && pi.user_id=?\n" +
                "where p.id=?";
        int getPostParams = postId;
        int getPostParams2 = userId;
        return this.jdbcTemplate.queryForObject(getWorkQuery,
                (rs, rowNum) -> new PostDetail(
                        rs.getInt("p.id"),
                        rs.getString("profile_img"),
                        rs.getString("nick"),
                        rs.getString("town"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getString("date"),
                        rs.getInt("num"),
                        rs.getString("content"),
                        rs.getString("transaction_status"),
                        rs.getInt("interestStatus"),
                        rs.getString("p.created_at")
                ),
                getPostParams2, getPostParams);
    }

    public List<String> getPostImg(int postId){
        String getPostImgQuery =
                "select img\n" +
                "from post_image\n" +
                "where post_id=?";
        int getPostImgParams = postId;
        return this.jdbcTemplate.query(getPostImgQuery,
                (rs, rowNum) -> new String(
                        rs.getString("img")
                ),
                getPostImgParams);
    }
}
