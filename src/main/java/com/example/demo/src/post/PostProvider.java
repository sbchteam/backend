package com.example.demo.src.post;
import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.post.model.*;
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

    /**공구 목록 조회 API*/
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
            List<PostList> postLists = postDao.getPosts(userId);
            return postLists;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**공구 검색 api*/
    public List<PostList> getPostSearch(int userId,String word) throws BaseException{
        try{
            List<PostList> postLists = postDao.getPostSearch(userId,word);
            return postLists;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**키워드 등록 api*/
    public String PostKeyword(int userId,String word) throws BaseException{
        try{
            if(userId==0){
                throw new BaseException(EMPTY_JWT);
            }
            postDao.PostKeyword(userId,word);
            return word;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 공구 상세 페이지 API*/
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
    /**공구 추천하기 */
    public List<PostRecommend> getPostsRecommend(int postId) throws BaseException{
        try{
            List<PostRecommend> postRecommends = postDao.getPostsRecommend(postId);
            return postRecommends;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 작품에 관심 누르기 API*/
    public PostInterest PushPostInterest(int postId,int userId) throws BaseException {
        //중복
        try{
            PostInterest postInterest;
            postInterest = postDao.PushPostInterest(postId,userId);
            return postInterest;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 거래상황변경 API*/
    public PostDetailImg changeTranslate(int postId,int userId,String translateStatus) throws BaseException {
        try{
            PostDetail postDetail = postDao.changeTranslate(postId,userId,translateStatus);
            List<String> imgUrls=postDao.getPostImg(postId);

            PostDetailImg postDetailImg=new PostDetailImg();

            postDetailImg.setPostDetail(postDetail);
            postDetailImg.setImgUrls(imgUrls);
            return postDetailImg;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /*공구게시글 신고하기*/
    public PostInterest PostReport(int postId,int userId) throws BaseException{
        try{
            PostInterest postReport = postDao.PostReport(postId,userId);
            return postReport;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
