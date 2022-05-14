package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.Post;
import com.example.demo.src.post.model.PostDetail;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**공구 게시물 작성 API
     **/
    public PostDetail create(Post post) throws BaseException{
         try {
             PostDetail postDetail = postDao.createPost(post);
             return postDetail;
         } catch (Exception exception) {
             throw new BaseException(DATABASE_ERROR);
         }
    }

    /**공구 게시물 수정 API*/
    public void update(Post post, int postId) {
        postDao.updatePost(post, postId);
    }

    /**공구 게시물 삭제 API*/
    public void delete(int postId) {
        postDao.deletePost(postId);
    }

    /**공구 게시물 카테고리 선택 API*/
    public String postCategory(int postId) {
        String category = postDao.getCategory(postId);
        return category;
    }

    /**공구 게시물 날짜 및 시간 선택 API*/
    //여기는 PostProvider 양식 따라해봤어요
    public String postDate(int postId) throws BaseException {
        try {
            String date = postDao.getDate(postId);
            return date;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
