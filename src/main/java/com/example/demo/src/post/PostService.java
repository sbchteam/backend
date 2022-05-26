package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class PostService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final PostProvider postProvider;
    private final JwtService jwtService;


    @Autowired
    public PostService(PostDao postDao, PostProvider postProvider, JwtService jwtService) {
        this.postDao = postDao;
        this.postProvider = postProvider;
        this.jwtService = jwtService;

    }

    /** 공구 게시물 작성 API*/
    public PostDetail create(Post post, int userId) throws BaseException {
        try {
            PostDetail postDetail = postDao.createPost(post, userId);
            return postDetail;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 공구 게시물 수정 API*/
    public PostDetailImg update(Post post, int postId) throws BaseException {
        if (post.getUserId() == 0)
            throw new BaseException(EMPTY_JWT);

        try {
            Post existedPost = postDao.getPostObject(postId);
            post.setNullPost(existedPost);
            postDao.updatePost(post, postId);
            PostDetailImg modifiedPost = postProvider.getPost(postId, post.getUserId());
            return modifiedPost;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 공구 게시물 삭제 API*/
    public void delete (int postId, int userId) {
        postDao.deletePost(postId, userId);
    }

    /** 댓글 작성 API*/
    public PostCommentList createComment(PostComment postComment, int userId) throws BaseException {
        try {
            PostCommentList postCommentList = postDao.createComment(postComment, userId);
            return postCommentList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /** 댓글 수정 API*/
    public void updateComment(PostComment postComment, int commentId, int userId) throws BaseException {
        postDao.updateComment(postComment, commentId, userId);
    }

    /** 댓글 삭제 API*/
    public void deleteComment(int commentId, int userId) {
        postDao.deleteComment(commentId, userId);
    }
}
