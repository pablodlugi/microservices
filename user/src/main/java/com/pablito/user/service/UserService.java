package com.pablito.user.service;

import com.pablito.user.domain.dao.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User save(User user);

    User update(User user, Long id);

    void delete(Long id);

    User getUserById(Long id);

    Page<User> getPage(Pageable pageable);

    User getCurrentUser();

    void confirmUser(String token);
}
