package com.pablito.basket.client;

import com.pablito.common.dto.ProductDto;
import com.pablito.common.dto.UserDto;
import org.apache.http.Header;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/users/current")
    UserDto getCurrentUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);
}
