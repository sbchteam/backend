package com.example.demo.src.s3;

import com.example.demo.src.post.PostDao;
import com.example.demo.src.user.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class S3Controller {

    private final S3Uploader s3Uploader;
    @Autowired
    private final PostDao postDao;

    @Autowired
    private final UserDao userDao;

    @PostMapping("/images")
    public String upload(@RequestParam("img") String img, @RequestParam(required=false) int id, String imgType) throws IOException {
        String imgUrl="";
        if(imgType.equals("post")){
           // imgUrl=s3Uploader.uploadPost(multipartFile,id); //postId넣어줌
            postDao.putPostImage(id,img);
            imgUrl="사진등록성공";
        }
        else if(imgType.equals("profile")){
            //imgUrl=s3Uploader.uploadProfile(multipartFile,id); //userId넣어줌
            userDao.putProfileImage(id,img);
            imgUrl="프로필사진등록성공";
        }
        return imgUrl;
    }
}