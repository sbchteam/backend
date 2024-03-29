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
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.POST_INVALID_WORD;
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
    public BaseResponse<List<PostList>> getPosts(@RequestParam(required = false) String sort, String town){
        try {
            int userId = jwtService.getUserIdx();
            if (sort.equals("interest")) {
                List<PostList> postLists = postProvider.getPostsInterest(userId,town);
                return new BaseResponse<>(postLists);
            }else if(sort.equals("ongoing")){
                List<PostList> postLists = postProvider.getPostsOngoing(userId,town);
                return new BaseResponse<>(postLists);
            }else{
                List<PostList> postLists = postProvider.getPosts(userId,town);
                return new BaseResponse<>(postLists);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    /**
     * 공구 목록 카테고리별 조회 api
     * */
    @ResponseBody
    @GetMapping("/sort/{categoryId}")
    public BaseResponse<List<PostList>> getCategoryPosts(@PathVariable("categoryId") int categoryId, String town){
        try {
            int userId = jwtService.getUserIdx();
            List<PostList> postLists = postProvider.getCategoryPosts(userId,categoryId,town);
            return new BaseResponse<>(postLists);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 공구 검색 api
     */
    @ResponseBody
    @GetMapping("/search")
    public BaseResponse<List<PostList>> getPostSearch(@RequestParam(required = false) String word){
        try {
            int userId = jwtService.getUserIdx();
            List<PostList> postLists = postProvider.getPostSearch(userId,word);
            return new BaseResponse<>(postLists);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    /**
     * 키워드 등록 api
     */
    @ResponseBody
    @GetMapping("/keyword")
    public BaseResponse<String> PostKeyword(@RequestParam(required = false) String word){
        try {
            int userId = jwtService.getUserIdx();
            String keyword = postProvider.PostKeyword(userId,word);
            return new BaseResponse<>(keyword);

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
            int userId = jwtService.getUserIdx();
            List<PostRecommend> postRecommends = postProvider.getPostsRecommend(postId,userId);
            return new BaseResponse<>(postRecommends);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 공구 검색페이지 동네 인기 공구 추천(밑) api
     * [Get] /posts/recommend */
    @ResponseBody
    @GetMapping("/recommend") // (GET) 127.0.0.1:9000/app/products/:id 해도 됨...
    public BaseResponse<List<PostRecommend>> getSearchRecommend(@RequestParam(required = false) String town) {
        try{
            int userId = jwtService.getUserIdx();
            List<PostRecommend> postRecommends = postProvider.getSearchRecommend(town,userId);
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
     * [Get] /posts/report/:postId
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
     * [POST] /posts/save
     */
    @ResponseBody
    @PostMapping("/save")
    public BaseResponse<PostDetail> savePost(@RequestBody Post post) {
        try {
            String [] invalidWord={"술","소주","맥주","담배","막걸리","와인"};
            for (String word:invalidWord){
                if (post.getTitle().contains(word) || post.getProductName().contains(word) || post.getContent().contains(word)){
                    return new BaseResponse<>(POST_INVALID_WORD);
                }
            }
            int userId = jwtService.getUserIdx();
            PostDetail savePost = postService.create(post, userId);
            return new BaseResponse<>(savePost);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 공구 게시글 수정 API
     * [PATCH] /posts/:postId
     */
    @ResponseBody
    @PatchMapping("/{postId}")
    public PostDetailImg update (@RequestBody Post post, @PathVariable int postId) throws Exception {
        int userId = jwtService.getUserIdx();
        post.setUserId(userId);
        PostDetailImg postDetailImg = postService.update(post, postId);
        return postDetailImg;
    }

    /**
     * 공구 게시글 삭제 API
     * [DELETE] /posts/:postId
     */
    @ResponseBody
    @DeleteMapping("/{postId}")
    public String delete (@PathVariable int postId) throws Exception {
        int userId = jwtService.getUserIdx();
        postService.delete(postId, userId);
        return "redirect:";
    }

    /**
     * 공구 게시글 카테고리 조회 API
     * [GET] /posts/category
     */
    @ResponseBody
    @GetMapping("/category")
    public List<Category> getCategory() throws Exception {
        List<Category> category = postProvider.categoryList();
        return category;
    }

    /**
     * 공구 게시글 위치 조회 API
     * [GET] /posts/location
     */
    @ResponseBody
    @GetMapping("/location")
    public List<Location> getLocation() throws Exception {
        int userId = jwtService.getUserIdx();
        List<Location> location = postProvider.getLocation(userId);
        return location;
    }

    /**
     * 공구에 참여누르기/취소하기 API
     * [Get] /posts/join/:postId*/
    @ResponseBody
    @GetMapping("/join/{postId}")
    public BaseResponse<String> PostJoin(@PathVariable(value = "postId") int postId) {

        try{
            int userId = jwtService.getUserIdx();
            String result = postProvider.PostJoin(postId,userId);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공구 참여수락하기 API
     * [Get] /posts/joinApply*/
    @ResponseBody
    @GetMapping("/joinApply")
    public BaseResponse<String> PostJoinApply(@RequestParam(required = false) int postId, int userId) {

        try{
            String result = postProvider.PostJoinApply(postId,userId);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공구 참여 거절하기/ 주최자가 참여 취소하기 API
     * [Get] /posts/joinRefuse*/
    @ResponseBody
    @GetMapping("/joinRefuse")
    public BaseResponse<String> PostJoinRefuse(@RequestParam(required = false) int postId, int userId, String select) {

        try{
            String result = postProvider.PostJoinRefuse(postId,userId, select);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 공구 참여자 목록 보기 API
     * [Get] /posts/joinlist/:postId*/
    @ResponseBody
    @GetMapping("/joinlist/{postId}")
    public BaseResponse<List<JoinList>> PostJoinList(@RequestParam(required = false) String onlyJoin, @PathVariable(value = "postId") int postId) {
        try{
            int userId = jwtService.getUserIdx();
            if(onlyJoin.equals("ok")){
                List<JoinList> result = postProvider.PostJoinOnlyList(postId,userId);
                return new BaseResponse<>(result);
            }
            List<JoinList> result = postProvider.PostJoinList(postId,userId);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글 작성 API
     * [POST] /posts/comment
     */
    @ResponseBody
    @PostMapping("/comment")
    public BaseResponse<PostCommentList> createComment(@RequestBody PostComment postComment) {
        try {
            int userId = jwtService.getUserIdx();
            PostCommentList postCommentList = postService.createComment(postComment, userId);
            return new BaseResponse<>(postCommentList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 댓글 조회 API
     * [GET] /posts/comment
     */
    @ResponseBody
    @GetMapping("/comment/{postId}")
    public BaseResponse<List<PostCommentList>> postCommentList(@PathVariable(value = "postId") int postId) {
        try {
            List<PostCommentList> result = postProvider.postCommentLists(postId);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 댓글 수정 API
     * [PATCH] /posts/comment
     */
    @ResponseBody
    @PatchMapping("/comment/{commentId}")
    public String updateComment(@RequestBody PostComment postComment,@PathVariable(value = "commentId") int commentId) throws Exception {
        int userId = jwtService.getUserIdx();
        postService.updateComment(postComment, commentId, userId);
        return "redirect:/comment";
    }

    /**
     * 댓글 삭제 API
     * [DELETE] /posts/comment
     */
    @ResponseBody
    @DeleteMapping("/comment/{commentId}")
    public String deleteComment (@PathVariable(value = "commentId") int commentId) throws Exception {
        int userId = jwtService.getUserIdx();
        postService.deleteComment(commentId, userId);
        return "redirect:/comment";
    }
}
