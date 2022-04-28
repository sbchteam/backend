package com.example.demo.src.post;
import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.post.model.Post;
import com.example.demo.src.post.model.PostDetail;
import com.example.demo.src.post.model.PostDetailImg;
import com.example.demo.src.post.model.PostList;
import com.example.demo.src.user.UserDao;
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
public class PostProvider {

    private final PostDao postDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }

    public List<PostList> getPostsInterest(int userId) throws BaseException{
        try{
            List<PostList> postLists = postDao.getPostsInterest(userId);
            return postLists;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<PostList> getPostsOngoing(int userId) throws BaseException{
        try{
            List<PostList> postLists = postDao.getPostsOngoing(userId);
            return postLists;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<PostList> getPosts(int userId) throws BaseException{
        try{
            System.out.println("빈칸2");
            List<PostList> postLists = postDao.getPosts(userId);
            System.out.println("빈칸3");
            return postLists;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostDetailImg getPost(int postId, int userId) throws BaseException {
        try {
            PostDetail postDetail=postDao.getPost(postId,userId);
            List<String> imgUrls=postDao.getPostImg(postId);


            PostDetailImg postDetailImg=new PostDetailImg();

            postDetailImg.setPostDetail(postDetail);
            postDetailImg.setImgUrls(imgUrls);

            return postDetailImg;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}