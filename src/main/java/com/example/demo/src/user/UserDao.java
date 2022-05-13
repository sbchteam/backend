package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<User> getUsers(){
        String getUsersQuery = "select * from user";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("nick"),
                        rs.getInt("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("right_status"),
                        rs.getString("profile_img"),
                        rs.getString("social"))
                );
    }
    public User getUser(PostLoginReq postLoginReq){
        String getUserQuery = "select * from user where email = ?";
        String getUserParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("nick"),
                        rs.getInt("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("right_status"),
                        rs.getString("profile_img"),
                        rs.getString("social")
                ),
                getUserParams
        );

    }
    /*프로필 조회*/
    public UserProfile getUserProfile(int userId){
        String getUserQuery = "" +
                "select u.id,nick,name,phone,profile_img,avg(uc.score) as credibility_score\n" +
                "from user u\n" +
                "join user_credibility uc on u.id = uc.user_id\n" +
                "where u.id=?\n" +
                "group by u.id";
        int getUserParams = userId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new UserProfile(
                        rs.getInt("u.id"),
                        rs.getString("name"),
                        rs.getString("nick"),
                        rs.getString("phone"),
                        rs.getString("profile_img"),
                        rs.getFloat("credibility_score")
                ),
                getUserParams);
    }
    /*주최한 공구 조회*/
    public List<UserPosts> getUserHost(int userId){
        String getUsersQuery = "" +
                "select post.id,title,category, price, post.created_at,img\n" +
                "from post\n" +
                "join category c on post.category_id = c.id\n" +
                "left join post_image pi on post.id = pi.post_id\n" +
                "where user_id=? && post.status=1\n" +
                "group by post.id;";
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy년 MM월 dd일");
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new UserPosts(
                        rs.getInt("post.id"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        dateFormat.format(rs.getTimestamp("post.created_at")),
                        rs.getString("img")
                ),userId
        );
    }
    /*받은 후기 조회*/
    public List<UserReviews> getUserReview(int userId){
        String getUsersQuery = "" +
                "select u.id,profile_img,nick,content\n" +
                "from user_credibility uc\n" +
                "join user u on uc.evaluator_id = u.id\n" +
                "where uc.user_id=?";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new UserReviews(
                        rs.getInt("u.id"),
                        rs.getString("profile_img"),
                        rs.getString("content"),
                        rs.getString("nick")
                ),userId
          );
    }
   /*찜한 공구 조회*/
    public List<UserPosts> getUserInterest(int userId){
        String getUsersQuery = "" +
                "select post.id,title,category, price, post.created_at,img\n" +
                "from post\n" +
                "join post_interest p on post.id = p.post_id && p.status=1\n" +
                "join category c on post.category_id = c.id\n" +
                "left join post_image pi on post.id = pi.post_id\n" +
                "where p.user_id=?\n" +
                "group by post.id;";
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy년 MM월 dd일");
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new UserPosts(
                        rs.getInt("post.id"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        dateFormat.format(rs.getTimestamp("post.created_at")),
                        rs.getString("img")
                ),userId
        );
    }
    /*참여한 공구 조회*/
    public List<UserPosts> getUserJoin(int userId){
        String getUsersQuery = "" +
                "select post.id,title,category, price, post.created_at,img\n" +
                "from post\n" +
                "join post_join p on post.id = p.post_id && p.status=1\n" +
                "join category c on post.category_id = c.id\n" +
                "left join post_image pi on post.id = pi.post_id\n" +
                "where p.user_id=?\n" +
                "group by post.id;";
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy년 MM월 dd일");
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new UserPosts(
                        rs.getInt("post.id"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        dateFormat.format(rs.getTimestamp("post.created_at")),
                        rs.getString("img")
                ),userId
        );
    }
    public UserAddress getUserAddress(int locationId){
        String getUserAddressQuery = "select * from user_location where id = ?";

        return this.jdbcTemplate.queryForObject(getUserAddressQuery,
                (rs,rowNum)-> new UserAddress(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getFloat("longitude"),
                        rs.getFloat("latitude"),
                        rs.getString("province"),
                        rs.getString("city"),
                        rs.getString("town"),
                        rs.getString("country"),
                        rs.getInt("status"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getInt("choice_status"),
                        rs.getInt("certify_status")
                ),
                locationId
        );

    }
    public UserAddress certifyUserAddress(int locationId, int userId,int certifyStatus){
        String certifyUserAddressQuery = "update user_location set certify_status=? where id=? && user_id=?";
        Object[] certifyUserAddressParams = new Object[]{certifyStatus,locationId,userId};
        this.jdbcTemplate.update(certifyUserAddressQuery, certifyUserAddressParams);

        UserAddress userAddress=getUserAddress(locationId);
        return userAddress;

    }
    public UserAddress createUserAddress(String region1, String region2, String region3, int userId){
        String createUserAddressQuery = "insert into user_location (user_id, province, city, town) VALUES (?,?,?,?)";
        Object[] createUserAddressParams = new Object[]{userId,region1,region2,region3};
        this.jdbcTemplate.update(createUserAddressQuery, createUserAddressParams);

        String lastInsertIdQuery = "select last_insert_id()";
        int locationId=this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);

        UserAddress userAddress=getUserAddress(locationId);
        return userAddress;
    }


    public PostUserRes createUser(PostUserReq postUserReq){
        if(postUserReq.getSocial()==null) {
            String createUserQuery = "insert into user (email, name, phone, password, nick) VALUES (?,?,?,?,?)";
            Object[] createUserParams = new Object[]{postUserReq.getEmail(),postUserReq.getName(),postUserReq.getPhone(),postUserReq.getPassword(),postUserReq.getNick()};
            this.jdbcTemplate.update(createUserQuery, createUserParams);
        }else if(postUserReq.getSocial()=="kakao"){
            String createUserQuery = "insert into user (email, name, phone, password, nick,social) VALUES (?,?,?,?,?,?)";

            Object[] createUserParams = new Object[]{postUserReq.getEmail(),postUserReq.getName(),postUserReq.getPhone(),postUserReq.getPassword(),postUserReq.getNick(),postUserReq.getSocial()};
            this.jdbcTemplate.update(createUserQuery, createUserParams);
        }

        String lastInsertIdQuery = "select last_insert_id()";
        String getNameQuery="select id, nick from user where id=?";
        int getNameParams=this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
        return this.jdbcTemplate.queryForObject(getNameQuery,
                (rs, rowNum) -> new PostUserRes(
                        rs.getInt("id"),
                        rs.getString("nick"),
                        rs.getString("nick")
                ),
                getNameParams);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from user where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserProfile(UserProfile userProfile){
        String modifyUserProfileQuery = "update user set name = ?, nick=?, phone=? where id = ? ";
        Object[] modifyUserProfileParams = new Object[]{userProfile.getName(),userProfile.getNick(),userProfile.getPhone(),userProfile.getId()};

        return this.jdbcTemplate.update(modifyUserProfileQuery,modifyUserProfileParams);
    }

    /*신뢰도, 후기 평가*/
    public UserProfile setUserEvaluation(UserEvaluation userEvaluation, int userId){

        String setUserEvaluationQuery = "insert into user_credibility(user_id,evaluator_id,score, content) VALUES (?,?,?,?)";
        Object[] setUserEvaluationParams = new Object[]{userEvaluation.getUserId(),userId,userEvaluation.getScore(),userEvaluation.getContent()};
        this.jdbcTemplate.update(setUserEvaluationQuery, setUserEvaluationParams);
        return getUserProfile(userEvaluation.getUserId());
    }
    /*신뢰도평가,후기작성 전 채팅한적 있는 유저인지 검사*/


    /*유저 차단하기*/
    public UserBlock UserBlock(int blockUserId,int userId){

        String setUserBlockQuery = "insert into user_block (user_id,blocker_id) VALUES (?,?)";
        Object[] setUserBlockParams = new Object[]{blockUserId,userId};
        this.jdbcTemplate.update(setUserBlockQuery, setUserBlockParams);

        String lastInsertIdQuery = "select last_insert_id()";
        String getUserBlockQuery="select * from user_block where id=?";
        int getUserBlockParams=this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
        return this.jdbcTemplate.queryForObject(getUserBlockQuery,
                (rs, rowNum) -> new UserBlock(
                        rs.getInt("user_id"),
                        rs.getInt("blocker_id"),
                        rs.getInt("status")
                ),
                getUserBlockParams);
    }

    //user에 imgUrl넣음
    public void putProfileImage(int userId,String imgurl){
        String putProfileImageQuery = "update user set profile_img=? where id=?";
        Object[] putProfileImageParams = new Object[]{imgurl,userId};
        this.jdbcTemplate.update(putProfileImageQuery, putProfileImageParams);
    }

    //키워드 검색 api
    public List<String> getKeyword(int userId){
        String getKeywordQuery = "select * from user_keyword where user_id=?";
        Object[] getKeywordParams = new Object[]{userId};
        return this.jdbcTemplate.query(getKeywordQuery,
                (rs, rowNum) -> new String(
                        rs.getString("keyword")
                ),
                getKeywordParams);
    }
}
