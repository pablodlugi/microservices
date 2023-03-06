package com.pablito.user.security.controller;

import com.pablito.user.domain.dto.LoginDto;
import com.pablito.user.domain.dto.TokenDto;
import com.pablito.user.security.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logins")
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public TokenDto login(@RequestBody @Valid LoginDto login) {
        return loginService.login(login);
    }
}
