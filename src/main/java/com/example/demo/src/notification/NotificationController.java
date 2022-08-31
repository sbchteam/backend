package com.example.demo.src.notification;

import com.example.demo.src.notification.model.NotificationReq;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app")
public class NotificationController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NotificationService notificationService;

    @PostMapping("/fcm")
    public ResponseEntity pushMessage(@RequestBody NotificationReq notificationReq) throws IOException {
        System.out.println(notificationReq.getTargetToken() + " "
                + notificationReq.getTitle() + " " + notificationReq.getBody());

        notificationService.sendMessageTo(
                notificationReq.getTargetToken(),
                notificationReq.getTitle(),
                notificationReq.getBody());
        return ResponseEntity.ok().build();
    }
}
