package com.example.demo.src.user.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAddress {
    private int id;
    private int userId;
    private float longitude;
    private float latitude;
    private String province;
    private String city;
    private String town;
    private String country;
    private int status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int choiceStatus;
    private int certifyStatus;
}
