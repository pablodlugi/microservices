package com.pablito.basket.client;

import com.pablito.common.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClientFacade {

    private final UserClient userClient;

    @CircuitBreaker(name = "backendA", fallbackMethod = "currentUserFallback")
    public UserDto getCurrentUser(String token) {
        return userClient.getCurrentUser(token);
    }

    public UserDto currentUserFallback(String token, Exception e) {
        log.warn("EXCEPTION: ", e);
        return new UserDto();
    }
}
