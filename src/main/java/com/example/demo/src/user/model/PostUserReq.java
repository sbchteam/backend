package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private int id;
    private String email;
    private String name;
    private String phone;
    private String password;
    private String password2;
    private String nick;
    private String social;
}
