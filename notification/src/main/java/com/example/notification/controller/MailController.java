package com.example.notification.controller;

import com.example.notification.service.MailService;
import com.pablito.common.dto.MailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mails")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    //    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    @PostMapping
    public void sendMail(@RequestBody MailDto mailDto) {
        mailService.send(mailDto.receiveAddress(), mailDto.templateName(), mailDto.variables());
    }

}
