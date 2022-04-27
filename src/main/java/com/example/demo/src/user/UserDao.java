package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
    public UserAddress certifyUserAddress(int locationId, int userId){
        String certifyUserAddressQuery = "update user_location set certify_status=? where id=? && user_id=?";
        Object[] certifyUserAddressParams = new Object[]{1,locationId,userId};
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

    public UserProfile getUserProfile(int userId){
        String getUserQuery = "select * from user where id = ?";
        int getUserParams = userId;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new UserProfile(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("nick"),
                        rs.getString("phone"),
                        rs.getString("profile_img")),
                getUserParams);
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





}
