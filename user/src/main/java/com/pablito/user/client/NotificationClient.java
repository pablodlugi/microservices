package com.pablito.user.client;

import com.pablito.common.dto.MailDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service") //FeignClient ma zaimplementowany LoadBalancer -> RoundRobin
public interface NotificationClient {

    @PostMapping("/api/v1/mails")
    @Async
    void sendMail(@RequestBody MailDto mailDto);
}
