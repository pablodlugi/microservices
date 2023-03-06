package com.pablito.user.repository;


import com.pablito.user.domain.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, RevisionRepository<User, Long, Integer> {

    Optional<User> findByEmailAndTokenIsNullOrUsernameAndTokenIsNull(String email, String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByToken(String token);
}
