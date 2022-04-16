package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserProfile {
    private int id;
    private String name;
    private String nick;
    private String phone;
    private String profileImg;

    public void setNullProfile(UserProfile userProfile){
        if(this.getName()==null){
            this.setName(userProfile.getName());
        }
        if(this.getNick()==null){
            this.setNick(userProfile.getNick());
        }
        if(this.getPhone()==null){
            this.setPhone(userProfile.getPhone());
        }
        if(this.getProfileImg()==null){
            this.setProfileImg(userProfile.getProfileImg());
        }
    }
}
