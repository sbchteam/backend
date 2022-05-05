package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public List<User> getUsers() throws BaseException{
        try{
            List<User> users = userDao.getUsers();
            return users;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    /*프로필 조회*/
    public UserProfile getUserProfile(int userId) throws BaseException {
        if(userId==0){
            throw new BaseException(EMPTY_JWT);
        }
        try {
            UserProfile userProfile = userDao.getUserProfile(userId);
            return userProfile;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /*주최한 공구 조회*/
    public List<UserPosts> getUserHost(int userId) throws BaseException {
        if(userId==0){
            throw new BaseException(EMPTY_JWT);
        }
        try {
            List<UserPosts> userPosts = userDao.getUserHost(userId);
            return userPosts;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /*받은 후기 조회*/
    public List<UserReviews> getUserReview(int userId) throws BaseException {
        if(userId==0){
            throw new BaseException(EMPTY_JWT);
        }
        try {
            List<UserReviews> userReviews = userDao.getUserReview(userId);
            return userReviews;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /*유저 차단하기*/
    public UserBlock UserBlock(int blockUserId,int userId) throws BaseException{
        try{
            UserBlock userBlock = userDao.UserBlock(blockUserId,userId);
            return userBlock;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        User user = userDao.getUser(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(postLoginReq.getPassword().equals(password)){
            int userId = userDao.getUser(postLoginReq).getId();
            String jwt = jwtService.createJwt(userId);
            return new PostLoginRes(userId,jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }

    }

}
