package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
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
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    /*신뢰도+평가 api*/
    public UserEvaluation setUserEvaluation(UserEvaluation userEvaluation,int userId) throws BaseException {
        try{
            /*validation처리 해야됨. 채팅 한적 있는 사람만 dao에 검사하는 메소드 두고 검사해서 1나오면 가능하게하고 0이면 불가능하게 하면될듯*/
            UserEvaluation userEvaluationRes = userDao.setUserEvaluation(userEvaluation,userId);
            return userEvaluationRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /*회원가입*/
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkEmail(postUserReq.getEmail()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try{
            //암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            PostUserRes postUserRes = userDao.createUser(postUserReq);
            String jwt = jwtService.createJwt(postUserRes.getId());
            postUserRes.setJwt(jwt);
            return postUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public UserProfile modifyUserProfile(UserProfile userProfile) throws BaseException {
        if(userProfile.getId()==0){
            throw new BaseException(EMPTY_JWT);
        }
        try {
            UserProfile userExistprofile = userDao.getUserProfile(userProfile.getId()); //아이디 값 가진 user profile정보 불러옴
            userProfile.setNullProfile(userExistprofile);
            int result = userDao.modifyUserProfile(userProfile);
            if (result == 0) {
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
            UserProfile userModifiedProfile = userDao.getUserProfile(userProfile.getId());
            return userModifiedProfile;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /*동면읍 검색*/
    public AddressSearchList getAddressSearch(String dong,int userId){
        List<String> searchResult=new ArrayList<String>();
        AddressSearchList addressSearchList=new AddressSearchList();
        try {
            // 파라미터를 사용하여 요청 URL을 구성한다.
            String apiURL = "https://dapi.kakao.com/v2/local/search/address.json?" +
                    "query=" + dong;

            HttpHeaders headers=new HttpHeaders();
            headers.add("Authorization","KakaoAK 9adca47b25d38d5f1826188403e6caca");

            RestTemplate restTemplate=new RestTemplate();
            ResponseEntity<String> result=restTemplate.exchange(apiURL, HttpMethod.GET,new HttpEntity<>(headers),String.class);

            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject=(JSONObject)jsonParser.parse(result.getBody());
            JSONArray jsonArray=(JSONArray)jsonObject.get("documents");
            List<AddressInfo> addressInfos=new ArrayList<>(jsonArray.size());

            for(int i=0;i<jsonArray.size();i++){
                AddressInfo addressInfo=new AddressInfo();

                JSONObject local=(JSONObject)jsonArray.get(i);
                searchResult.add((String)local.get("address_name"));
                JSONObject address=(JSONObject)local.get("address");
                //행정구역 상세 받기
                String region1=(String)address.get("region_1depth_name");
                String region2=(String)address.get("region_2depth_name");
                String region3_d=(String)address.get("region_3depth_name");
                String region3_h=(String)address.get("region_3depth_h_name");
                //region3 정리
                String region3=region3_d;
                if(!(region3_h.isEmpty())){
                    region3=region3_h;
                }

                addressInfo.setListId(i);
                addressInfo.setResion1(region1);
                addressInfo.setResion2(region2);
                addressInfo.setResion3(region3);

                addressInfos.add(addressInfo);
            }

            addressSearchList.setUserid(userId);
            addressSearchList.setAddressInfos(addressInfos);
            addressSearchList.setAddressList(searchResult);
            return addressSearchList;


        } catch (Exception e) {
            System.out.println(e);
            return addressSearchList;
        }

    }
    /*위치 등록*/
    public UserAddress UserAddressEnroll(String region1, String region2, String region3, int userId) throws BaseException {

        try {
            UserAddress userAddress=userDao.createUserAddress(region1,region2,region3,userId);
            return userAddress;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /*위치 인증*/
    public UserAddress UserAddressCertify(String lati, String longi,int userId,int locationId){
        UserAddress userAddress=userDao.getUserAddress(locationId);
        try{
            final String APPKEY="9adca47b25d38d5f1826188403e6caca";
            final String API_URL="https://dapi.kakao.com/v2/local/geo/coord2address.json?x="+longi+"&y="+lati+"&input_coord=WGS84";

            HttpHeaders headers=new HttpHeaders();
            headers.add("Authorization","KakaoAK 9adca47b25d38d5f1826188403e6caca");

            MultiValueMap<String,String> parameters=new LinkedMultiValueMap<String,String>();
            parameters.add("x",longi);
            parameters.add("y",lati);
            parameters.add("input_coord","WGS84");

            RestTemplate restTemplate=new RestTemplate();
            ResponseEntity<String> result=restTemplate.exchange(API_URL,HttpMethod.GET,new HttpEntity<>(headers),String.class);

            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject=(JSONObject)jsonParser.parse(result.getBody());
            JSONArray jsonArray=(JSONArray)jsonObject.get("documents");

            JSONObject local=(JSONObject)jsonArray.get(0);
            JSONObject jsonArray1=(JSONObject)local.get("address");
            String localAddress=(String)jsonArray1.get("address_name");

            //userdao에서 region1,2,3가져옴, 여기서 리턴되는 region1,2,3이랑 비교해서 같으면 인증 성공 다르면 인증실패
            String region1=(String)jsonArray1.get("region_1depth_name");
            String region2=(String)jsonArray1.get("region_2depth_name");
            String region3=(String)jsonArray1.get("region_3depth_name");

            int certifyStatus=0;
            if(region1.equals(userAddress.getProvince()) && region2.equals(userAddress.getCity()) && region3.equals(userAddress.getTown())){
                certifyStatus=1;
                userAddress=userDao.certifyUserAddress(userAddress.getId(),userId,certifyStatus);
            }else{
                userAddress=userDao.certifyUserAddress(userAddress.getId(),userId,certifyStatus);
            }


            return userAddress;
        }
        catch (Exception e){
            e.printStackTrace();
            return userAddress;
        }
    }

    public UserAddress UserAddressNow(String lati, String longi,int userId){
        UserAddress userAddress=new UserAddress();
        try{
            final String APPKEY="9adca47b25d38d5f1826188403e6caca";
            final String API_URL="https://dapi.kakao.com/v2/local/geo/coord2address.json?x="+longi+"&y="+lati+"&input_coord=WGS84";

            HttpHeaders headers=new HttpHeaders();
            headers.add("Authorization","KakaoAK 9adca47b25d38d5f1826188403e6caca");

            MultiValueMap<String,String> parameters=new LinkedMultiValueMap<String,String>();
            parameters.add("x",longi);
            parameters.add("y",lati);
            parameters.add("input_coord","WGS84");

            RestTemplate restTemplate=new RestTemplate();
            ResponseEntity<String> result=restTemplate.exchange(API_URL,HttpMethod.GET,new HttpEntity<>(headers),String.class);

            JSONParser jsonParser=new JSONParser();
            JSONObject jsonObject=(JSONObject)jsonParser.parse(result.getBody());
            JSONArray jsonArray=(JSONArray)jsonObject.get("documents");

            JSONObject local=(JSONObject)jsonArray.get(0);
            JSONObject jsonArray1=(JSONObject)local.get("address");
            String localAddress=(String)jsonArray1.get("address_name");

            //userdao에서 region1,2,3가져옴, 여기서 리턴되는 region1,2,3이랑 비교해서 같으면 인증 성공 다르면 인증실패
            String region1=(String)jsonArray1.get("region_1depth_name");
            String region2=(String)jsonArray1.get("region_2depth_name");
            String region3=(String)jsonArray1.get("region_3depth_name");

            userAddress=userDao.createUserAddress(region1,region2,region3,userId);
            UserAddress userAddress1=userDao.certifyUserAddress(userAddress.getId(),userId,1);


            return userAddress1;
        }
        catch (Exception e){
            e.printStackTrace();
            return userAddress;
        }
    }
}
