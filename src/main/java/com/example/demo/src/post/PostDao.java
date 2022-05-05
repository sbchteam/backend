package com.example.demo.src.post;

import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    String getPostsquery="" +
            "select p.id, title, category, price, num,transaction_status,p.created_at, \n" +
            "       max( case\n" +
            "       when pi.user_id =? then pi.status\n" +
            "       when pi.user_id!=? then 0\n" +
            "       when pi.user_id is null then 0\n" +
            "       end ) as interest_status,\n" +
            "       ifnull(interestNum,0) as interest_num,\n" +
            "        case\n" +
            "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 24\n" +
            "               then case\n" +
            "                        when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 1\n" +
            "                            then concat(timestampdiff(minute, p.created_at, current_timestamp()), ' 분전')\n" +
            "                        when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) >= 1\n" +
            "                            then concat(timestampdiff(hour, p.created_at, current_timestamp()), ' 시간전')\n" +
            "               end\n" +
            "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 48\n" +
            "               then '어제'\n" +
            "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 72\n" +
            "               then '그저께'\n" +
            "           else concat(-datediff(p.created_at, current_timestamp()), ' 일전')\n" +
            "        end    as timediff,\n" +
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
            ") incnt on p.id=incnt.post_id\n";


    public List<PostList> getPosts(int userId) {
        String getPostsQuery ="" +
                "select *\n" +
                "from(\n" +
                getPostsquery+
                "where p.status=1 && TIMESTAMPDIFF(minute , now(),p.date)>0 && p.user_id not in (\n" +
                "    select user_id\n" +
                "    from user_block\n" +
                "    where blocker_id=?\n" +
                ")\n"+
                "group by p.id) plist\n" +
                "order by plist.created_at desc;";

        int getPostsParams = userId;
        return this.jdbcTemplate.query(getPostsQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("timediff"),
                        rs.getString("img")
                ),
                getPostsParams, getPostsParams,getPostsParams
        );
    }
    public List<PostList> getPostsInterest(int userId) {
        String getPostsInterestQuery =
                "select *\n" +
                "from(\n" +
                getPostsquery+
                "where p.status=1 && TIMESTAMPDIFF(minute , now(),p.date)>0 && p.user_id not in (\n" +
                "    select user_id\n" +
                "    from user_block\n" +
                "    where blocker_id=?\n" +
                ")\n"+
                "group by p.id) plist\n " +
                "order by plist.interest_num desc";
        int getPostsInterestParams = userId;
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy년 MM월 dd일");
        return this.jdbcTemplate.query(getPostsInterestQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("timediff"),
                        rs.getString("img")
                ),
                getPostsInterestParams, getPostsInterestParams,getPostsInterestParams
        );
    }

    public List<PostList> getPostsOngoing(int userId) {
        String getPostsOngoingQuery =
                "select *\n" +
                "from(\n" +
                getPostsquery+
                "where p.transaction_status\n" +
                "not in ('complete') && p.status=1 && TIMESTAMPDIFF(minute , now(),p.date)>0 && p.user_id not in (\n" +
                "    select user_id\n" +
                "    from user_block\n" +
                "    where blocker_id=?\n" +
                ")\n" +
                "group by p.id) plist\n" +
                "order by plist.created_at desc;";
        int getPostsOngoingParams = userId;
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy년 MM월 dd일");
        return this.jdbcTemplate.query(getPostsOngoingQuery,
                (rs, rowNum) -> new PostList(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("transaction_status"),
                        rs.getInt("interest_status"),
                        rs.getInt("interest_num"),
                        rs.getString("timediff"),
                        rs.getString("img")
                ),
                getPostsOngoingParams, getPostsOngoingParams,getPostsOngoingParams
        );
    }

    //date처리, createdAt처리리
   public PostDetail getPost(int postId, int userId) {
        String getWorkQuery =
                "select p.id,p.user_id,u.profile_img,u.nick,town, category,title,price,date,num,content,transaction_status,ifnull(pi.status,0) as interestStatus,\n" +
                "       case\n" +
                "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 24\n" +
                "               then case\n" +
                "                        when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 1\n" +
                "                            then concat(timestampdiff(minute, p.created_at, current_timestamp()), ' 분전')\n" +
                "                        when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) >= 1\n" +
                "                            then concat(timestampdiff(hour, p.created_at, current_timestamp()), ' 시간전')\n" +
                "               end\n" +
                "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 48\n" +
                "               then '어제'\n" +
                "           when TIMESTAMPDIFF(hour, p.created_at, current_timestamp()) < 72\n" +
                "               then '그저께'\n" +
                "           else concat(-datediff(p.created_at, current_timestamp()), ' 일전')\n" +
                "        end    as created_at\n" +
                "from post p\n" +
                "join user u\n" +
                "on p.user_id = u.id\n" +
                "join user_location ul\n" +
                "on p.location_id = ul.id\n" +
                "join category c\n" +
                "on p.category_id = c.id\n" +
                "left join post_interest pi\n" +
                "on p.id = pi.post_id && pi.user_id=?\n" +
                "where p.id=?\n";
        int getPostParams = postId;
        int getPostParams2 = userId;
       SimpleDateFormat dateFormat= new SimpleDateFormat("MM월 dd일");
        return this.jdbcTemplate.queryForObject(getWorkQuery,
                (rs, rowNum) -> new PostDetail(
                        rs.getInt("p.id"),
                        rs.getInt("p.user_id"),
                        rs.getString("profile_img"),
                        rs.getString("nick"),
                        rs.getString("town"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getTimestamp("date"),
                        rs.getInt("num"),
                        rs.getString("content"),
                        rs.getString("transaction_status"),
                        rs.getInt("interestStatus"),
                        rs.getString("created_at")
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

    /*작품 추천 api*/
    public List<PostRecommend> getPostsRecommend(int PostId){
        String getPostCategory="" +
                "select category_id\n" +
                "from post\n" +
                "where post.id=?";
        int getPostId=PostId;
        int categoryId= this.jdbcTemplate.queryForObject(getPostCategory,
                int.class,
                getPostId);

        String getPostLocation="" +
                "select town\n" +
                "from user_location\n" +
                "join post p on user_location.id = p.location_id\n" +
                "where p.id=?;";
        String town= this.jdbcTemplate.queryForObject(getPostLocation,
                String.class,
                getPostId);

        String getPostQuery =
                "select p.id, title, price,img\n" +
                "from post p\n" +
                "join user_location ul on p.location_id = ul.id\n" +
                "left join post_image pi on p.id = pi.post_id\n" +
                "where town like ? && p.category_id=? && p.id!=? && p.transaction_status!='complete' && p.status=1 && TIMESTAMPDIFF(minute , now(),p.date)>0\n" +
                "group by p.id\n" +
                "limit 3;";
        String keyword='%'+town+'%';
        return this.jdbcTemplate.query(getPostQuery,
                (rs, rowNum) -> new PostRecommend(
                        rs.getInt("id"),
                        rs.getString("img"),
                        rs.getString("title"),
                        rs.getInt("price")
                ),keyword,categoryId,getPostId);
    }

    public PostInterest PushPostInterest(int postId, int userId){

        String checkInterestQuery = "" +
                "select EXISTS(\n" +
                "select *\n" +
                "from post_interest pi\n" +
                "where pi.post_id=? && pi.user_id=?) as exist";
        Object[] checkInterestParams = new Object[]{postId,userId};

        int result= this.jdbcTemplate.queryForObject(checkInterestQuery,
                int.class,
                checkInterestParams);


        //존재하면
        if(result==1){
            String checkStatusQuery = "" +
                    "select status\n" +
                    "from post_interest pi\n" +
                    "where pi.post_id=? && pi.user_id=?";
            Object[] checkStatusParams = new Object[]{postId,userId};

            int status= this.jdbcTemplate.queryForObject(checkStatusQuery,
                    int.class,
                    checkStatusParams);

            //관심이 눌린 상태면
            if(status==1){
                String clearInterestQuery = "update post_interest set status=0 where post_id=? && user_id=?";
                Object[] clearInterestParams = new Object[]{postId,userId};
                this.jdbcTemplate.update(clearInterestQuery, clearInterestParams);
            }else{
                String createInterestQuery = "update post_interest set status=1 where post_id=? && user_id=?";
                Object[] createInterestParams = new Object[]{postId,userId};
                this.jdbcTemplate.update(createInterestQuery, createInterestParams);
            }
        }
        else{
            String createInterestQuery = "insert into post_interest(post_id,user_id) VALUES (?,?)";
            Object[] createInterestParams = new Object[]{postId,userId};
            this.jdbcTemplate.update(createInterestQuery, createInterestParams);

        }
        String getInterestQuery="" +
                "select post_id,user_id,status\n" +
                "from post_interest\n" +
                "where post_id=? && user_id=?";
        Object[] getInterestParams = new Object[]{postId,userId};
        return this.jdbcTemplate.queryForObject(getInterestQuery,
                (rs, rowNum) -> new PostInterest(
                        rs.getInt("post_id"),
                        rs.getInt("user_id"),
                        rs.getInt("status")
                ),
                getInterestParams);
    }

    public PostDetail changeTranslate(int postId,int userId, String translateStatus){
        String changeTranslateQuery="update post set transaction_status=? where id=? && post.user_id=?";
        Object[] changeTranslateParams = new Object[]{translateStatus,postId,userId};
        this.jdbcTemplate.update(changeTranslateQuery, changeTranslateParams);
        return getPost(postId,userId);
    }

    public Post getPostObject(int postId){
        String getPostObjectQuery =
                "select *\n" +
                "from post\n" +
                "where id=?";
        int getPostObjectParams = postId;
        return this.jdbcTemplate.queryForObject(getPostObjectQuery,
                (rs, rowNum) -> new Post(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getInt("category_id"),
                        rs.getString("product_name"),
                        rs.getInt("price"),
                        rs.getInt("location_id"),
                        rs.getTimestamp("date"),
                        rs.getInt("num"),
                        rs.getString("content"),
                        rs.getString("transaction_status"),
                        rs.getInt("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                ),
                getPostObjectParams);
    }
}
