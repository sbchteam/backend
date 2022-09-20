package com.example.demo.src.notification.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationReq {

    private String targetToken;
    private String title;
    private String body;
}
