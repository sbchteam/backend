package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private int id;
    private String email;
    private String name;
    private String phone;
    private String password;
    private String nick;
    private int status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String rightStatus;
    private String profileImg;
    private String social;
}
