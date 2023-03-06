package com.pablito.user.controller;


import com.pablito.user.domain.dto.UserDto;
import com.pablito.user.mapper.UserMapper;
import com.pablito.user.service.UserService;
import com.pablito.user.validator.group.Create;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Validated(Create.class)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAnonymous() || hasRole('ADMIN')")
    public UserDto saveUser(@RequestBody @Valid UserDto user) {                                 //KAŻDY NIEZALOGOEANY + ADMIN
        return userMapper.toDto(userService.save(userMapper.toDao(user)));
    }

    @GetMapping("/confirm")
    public void confirmUser(@RequestParam String token) {
        userService.confirmUser(token);
    }

    @GetMapping("/current")
    public UserDto getCurrentUser(){
        return userMapper.toDto(userService.getCurrentUser());
    }

    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || @securityService.hasAccessToUser(#id))")
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public UserDto getUserById(@PathVariable Long id) {
        return userMapper.toDto(userService.getUserById(id));
    }

    @PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || @securityService.hasAccessToUser(#id))")
    @PutMapping("{id}")
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public UserDto updateUserById(@RequestBody @Valid UserDto user, @PathVariable Long id) {
        return userMapper.toDto(userService.update(userMapper.toDao(user), id));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("isAuthenticated() && (hasRole('ADMIN') || @securityService.hasAccessToUser(#id))")
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public void deleteUserById(@PathVariable Long id) {
        userService.delete(id);
    }

    @PreAuthorize("hasRole('ADMIN')") //spel spring expression language
    @GetMapping
    @Operation(security = @SecurityRequirement(name = "BEARER AUT TOKEN"))
    public Page<UserDto> getUserPage(@RequestParam int page, @RequestParam int size) {
        return userService.getPage(PageRequest.of(page, size)).map(userMapper::toDto);
    }
}
