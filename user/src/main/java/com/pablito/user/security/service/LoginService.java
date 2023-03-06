package com.pablito.user.security.service;

import com.pablito.user.domain.dto.LoginDto;
import com.pablito.user.domain.dto.TokenDto;

public interface LoginService {

    TokenDto login(LoginDto loginDto);

}
