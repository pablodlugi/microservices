package com.pablito.user.mapper;

import com.pablito.user.domain.dao.User;
import com.pablito.user.domain.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends AuditableMapper<User, UserDto> {
    User toDao(UserDto userDto);

    @Mapping(target = "password", ignore = true)
    UserDto toDto(User user);
}
