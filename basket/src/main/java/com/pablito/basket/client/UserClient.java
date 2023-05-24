package com.pablito.basket.client;

import com.pablito.common.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
interface UserClient {

    @GetMapping("/api/v1/users/current")
    UserDto getCurrentUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
