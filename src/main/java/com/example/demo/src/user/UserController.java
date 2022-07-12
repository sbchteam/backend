package com.example.demo.src.user;

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
import static com.example.demo.config.BaseResponseStatus.POST_USERS_ANOTHER_PASSWORD;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static com.example.demo.utils.ValidationRegex.isRegexPassword;

@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원 조회 API
     * [GET] /users
     * 회원 번호 및 이메일 검색 조회 API
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<User>> getUsers() {
        try{
            List<User> users = userProvider.getUsers();
            return new BaseResponse<>(users);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 프로필 조회 API
     * userId에 0을 넣으면 본인 프로필임
     * [GET] /users/profile
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/profile/{userId}")
    public BaseResponse<UserProfile> getUserProfile(@PathVariable(value = "userId") int userId) {
        // Get Users
        try{
            if(userId==0){
                userId = jwtService.getUserIdx();
            }
            UserProfile userProfile = userProvider.getUserProfile(userId);
            return new BaseResponse<>(userProfile);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 유저가 주최한 공구 조회 API
     *  userId에 0을 넣으면 본인 프로필임
     * [GET] /users/host/:userId
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/host/{userId}")
    public BaseResponse<List<UserPosts>> getUserHost(@PathVariable(value = "userId") int userId) {
        // Get Users
        try{
            if(userId==0){
                userId = jwtService.getUserIdx();
            }
            List<UserPosts> userPosts = userProvider.getUserHost(userId);
            return new BaseResponse<>(userPosts);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저가 받은 후기 조회 API
     *  userId에 0을 넣으면 본인 프로필임
     * [GET] /users/review/:userId
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/review/{userId}")
    public BaseResponse<List<UserReviews>> getUserReview(@PathVariable(value = "userId") int userId) {
        // Get Users
        try{
            if(userId==0){
                userId = jwtService.getUserIdx();
            }
            List<UserReviews> userReviews = userProvider.getUserReview(userId);
            return new BaseResponse<>(userReviews);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 유저가 찜한 공구 조회 API
     * [GET] /users/interest
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/interest")
    public BaseResponse<List<UserPosts>> getUserInterest() {
        try{
            int userId=jwtService.getUserIdx();
            List<UserPosts> userPosts = userProvider.getUserInterest(userId);
            return new BaseResponse<>(userPosts);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 유저가 참여한 공구 조회 API
     * [GET] /users/join
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/join")
    public BaseResponse<List<UserPosts>> getUserJoin() {
        try{
            int userId=jwtService.getUserIdx();
            List<UserPosts> userPosts = userProvider.getUserJoin(userId);
            return new BaseResponse<>(userPosts);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 동/면/읍 검색 api
     * /users/search
     * 입력받을 값 - 동/읍/면 검색 단어*/
    @ResponseBody
    @GetMapping("/location/search")
    public BaseResponse<AddressSearchList> getAddressSearch(@RequestParam(required = false) String dong) {
        try {
            int userId = jwtService.getUserIdx();
            AddressSearchList add = userService.getAddressSearch(dong,userId);
            return new BaseResponse<>(add);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 검색한 위치 중 선택 - 위치 등록 api
     * /users/search/choice
     * 입력받을 값 - region1, 2, 3*/
    @ResponseBody
    @GetMapping("/location/choice")
    public BaseResponse<UserAddress> UserAddressEnroll(@RequestParam(required = false) String region1, String region2, String region3) {
        try {
            int userId = jwtService.getUserIdx();
            UserAddress userAddress = userService.UserAddressEnroll(region1,region2,region3,userId);

            return new BaseResponse<>(userAddress);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 좌표로 주소 리턴(위치인증) api
     * /users/location/certify
     * 입력받을 값 - 현재 유저의 좌표와 인증할 위치정보id*/
    @ResponseBody
    @GetMapping("/location/certify")
    public BaseResponse<UserAddress> UserAddressCertify(@RequestParam(required = false) String longi, String lati,int locationId) {
        try {
            int userId = jwtService.getUserIdx();
            UserAddress userAddress = userService.UserAddressCertify(lati,longi,userId,locationId);
            return new BaseResponse<>(userAddress);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    /**
     * 현재위치등록 및 인증까지 api
     * /users/location/now
     * 입력받을 값 - 현재 유저의 좌표와 인증할 위치정보id*/
    @ResponseBody
    @GetMapping("/location/now")
    public BaseResponse<UserAddress> UserAddressNow(@RequestParam(required = false) String longi, String lati) {
        try {
            int userId = jwtService.getUserIdx();
            UserAddress userAddress = userService.UserAddressNow(lati,longi,userId);
            return new BaseResponse<>(userAddress);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if(postUserReq.getName()==null){
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        if(postUserReq.getPhone() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
        }
        if(postUserReq.getPassword()==null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(!isRegexPassword(postUserReq.getPassword())){
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        if(postUserReq.getPassword2()==null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD2);
        }
        if(!isRegexPassword(postUserReq.getPassword2())){
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        if(!postUserReq.getPassword().equals(postUserReq.getPassword2())){
            return new BaseResponse<>(POST_USERS_ANOTHER_PASSWORD);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            if(postLoginReq.getEmail()==null){
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if(!isRegexEmail(postLoginReq.getEmail())){
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            if(postLoginReq.getPassword() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
            if(!isRegexPassword(postLoginReq.getPassword())){
                return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
            }
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users
     * @return BaseResponse<String>
     */
        @ResponseBody
        @PatchMapping("")
        public BaseResponse<UserProfile> modifyUserProfile(@RequestBody UserProfile userProfile){
            try {
                int userId = jwtService.getUserIdx();
                userProfile.setId(userId);
                UserProfile patchProfile=userService.modifyUserProfile(userProfile);

                return new BaseResponse<>(patchProfile);
            } catch (BaseException exception) {
                return new BaseResponse<>((exception.getStatus()));
            }
        }

    /**
     * 유저신용도평가 & 후기작성 API
     * [POST] /users/evaluation
     */
    @ResponseBody
    @PostMapping("evaluation")
    public BaseResponse<UserProfile> setUserEvaluation(@RequestBody UserEvaluation userEvaluation){
        try {
            int userId = jwtService.getUserIdx();
            UserProfile userProfile=userService.setUserEvaluation(userEvaluation,userId);
            return new BaseResponse<>(userProfile);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 차단하기 API
     * [Get] /users/block/:userId
     */
    @ResponseBody
    @GetMapping("block/{userId}")
    public BaseResponse<UserBlock> UserBlock(@PathVariable(value = "userId") int blockUserId){
        try {
            int userId = jwtService.getUserIdx();
            UserBlock userBlock=userProvider.UserBlock(blockUserId,userId);
            return new BaseResponse<>(userBlock);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 키워드 조회 api
     */
    @ResponseBody
    @GetMapping("/keyword")
    public BaseResponse<List<String>> getKeyword(){
        try {
            int userId = jwtService.getUserIdx();
            List<String> keywords = userProvider.getKeyword(userId);
            return new BaseResponse<>(keywords);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
