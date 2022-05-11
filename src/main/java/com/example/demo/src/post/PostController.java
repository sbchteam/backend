package com.example.demo.src.post;

import com.example.demo.src.post.model.*;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.tree.RowMapper;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static com.example.demo.utils.ValidationRegex.isRegexPassword;

@RestController
@RequestMapping("/app/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;

    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService){
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }


    /**
     * 공구 목록 조회 api
     * interest - 관심 많은 순 조회
     * ongoing - 거래 완료된 공구를 제외하여 조회
     * 예외 - 최신순 조회*/
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<PostList>> getPosts(@RequestParam(required = false) String sort){
        try {
            int userId = jwtService.getUserIdx();
            if (sort.equals("interest")) {
                List<PostList> postLists = postProvider.getPostsInterest(userId);
                return new BaseResponse<>(postLists);
            }else if(sort.equals("ongoing")){
                List<PostList> postLists = postProvider.getPostsOngoing(userId);
                return new BaseResponse<>(postLists);
            }else{
                List<PostList> postLists = postProvider.getPosts(userId);
                return new BaseResponse<>(postLists);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 공구 상세 페이지(기본화면) api
     * [Get] /posts/:postId */
    @ResponseBody
    @GetMapping("/{postId}") // (GET) 127.0.0.1:9000/app/products/:id 해도 됨...
    public BaseResponse<PostDetailImg> getPost(@PathVariable("postId") int postId) {
        // Get Users
        try{
            int userIdx = jwtService.getUserIdx();
            PostDetailImg postDetailImg = postProvider.getPost(postId,userIdx);
            return new BaseResponse<>(postDetailImg);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공구 상세페이지 공구 추천(밑) api
     * [Get] /posts/:postId/recommend */
    @ResponseBody
    @GetMapping("/{postId}/recommend") // (GET) 127.0.0.1:9000/app/products/:id 해도 됨...
    public BaseResponse<List<PostRecommend>> getPostsRecommend(@PathVariable("postId") int postId) {
        // Get Users
        try{
            List<PostRecommend> postRecommends = postProvider.getPostsRecommend(postId);
            return new BaseResponse<>(postRecommends);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공구에 찜누르기/해제하기 API
     * [Get] /posts/interest/:postId*/
    @ResponseBody
    @GetMapping("/interest/{postId}")
    public BaseResponse<PostInterest> PushPostInterest(@PathVariable(value = "postId") int postId) {

        try{
            int userId = jwtService.getUserIdx();
            PostInterest interest = postProvider.PushPostInterest(postId,userId);
            return new BaseResponse<>(interest);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공구 거래상황변경 api
     * open - 모집중
     * deal - 거래중
     * complete - 거래완료*/
    @ResponseBody
    @GetMapping("/{postId}/translate")
    public BaseResponse<PostDetailImg> changeTranslate(@PathVariable("postId") int postId, @RequestParam(required = false) String status){
        try {
            if(!(status.equals("open")||status.equals("deal")||status.equals("complete"))){ //다른 값 들어오면 에러
                return new BaseResponse<>(POST_INVALID_TRANSLATE_CHANGE);
            }
            int userId = jwtService.getUserIdx();
            PostDetailImg postDetailImg = postProvider.changeTranslate(postId,userId,status);
            return new BaseResponse<>(postDetailImg);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공구 게시글 신고하기 API
     * [GET] /posts/report/:postId
     */
    @ResponseBody
    @GetMapping("report/{postId}")
    public BaseResponse<PostInterest> PostReport(@PathVariable(value = "postId") int postId){
        try {
            int userId = jwtService.getUserIdx();
            PostInterest postReport=postProvider.PostReport(postId,userId);
            return new BaseResponse<>(postReport);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공구 게시글 작성 API
     * [GET] /posts
     * */
    @ResponseBody
    @GetMapping("/postForm")
    public String write(Post post) throws Exception {
        postService.create(post);
        return "add-Post";
    }

    /**
     * 공구 게시글 작성처리 API
     * [POST] /posts/:postId
     */
    @PostMapping("/saved")
    public String posting(@ModelAttribute("post") Post post) throws Exception {
        postService.create(post);
        return "redirect:/:postId";
    }

    /**
     * 공구 게시글 카테고리 선택 API
     * [GET]
     */
    @ResponseBody
    @GetMapping("/postForm/category")
    public String getCategory(@PathVariable int postId) throws Exception {
        postService.postCategory(postId);
        return "postForm-category";
    }

    /**
     * 공구 게시글 날짜 및 시간 선택 API
     * [GET]
     */
    @ResponseBody
    @GetMapping("/postFrom/date")
    public String dateForm(@PathVariable int postId) throws Exception {
        postService.postDate(postId);
        return "postForm-date";
    }

    /**
     * 공구 게시글 수정 API
     */
    @ResponseBody
    @PostMapping("/update/{postId}")
    public String update(@ModelAttribute Post post, @PathVariable int postId) throws Exception {
        postService.update(post, postId);
        return "redirect:/:postId";
    }

    /**
     * 공구 게시글 삭제 APi
     */
    @ResponseBody
    @RequestMapping("/delete/{postId}")
    public String delete(@PathVariable int postId) throws Exception {
        postService.delete(postId);
        return "redirect:/:postId";
    }

}
