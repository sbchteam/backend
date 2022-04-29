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
     * [GET] /users? Email=
     * @return BaseResponse<List<GetUserRes>>
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
    public BaseResponse<List<User>> getUsers() {
        try{
            List<User> users = userProvider.getUsers();
            return new BaseResponse<>(users);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 1명 조회 API
     * [GET] /users/profile
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/profile") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<UserProfile> getUserProfile() {
        // Get Users
        try{
            int userId = jwtService.getUserIdx();
            UserProfile userProfile = userProvider.getUserProfile(userId);
            return new BaseResponse<>(userProfile);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 동/면/읍 검색 api
     * /users/search
     * 입력받을 값 - 동/읍/면 검색 단어*/
    @ResponseBody
    @GetMapping("/location/search") // (GET) 127.0.0.1:9000/app/users
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
    @GetMapping("/location/choice") // (GET) 127.0.0.1:9000/app/users
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
    @GetMapping("/location/certify") // (GET) 127.0.0.1:9000/app/users
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
    @GetMapping("/location/now") // (GET) 127.0.0.1:9000/app/users
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
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
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
     * [PATCH] /users/:userIdx
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


}
