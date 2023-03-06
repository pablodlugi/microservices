package com.pablito.user.service.impl;

import com.pablito.common.dto.MailDto;
import com.pablito.user.client.NotificationClient;
import com.pablito.user.domain.dao.User;
import com.pablito.user.repository.RoleRepository;
import com.pablito.user.repository.UserRepository;
import com.pablito.user.security.SecurityUtils;
import com.pablito.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor //wieloargumentowy konstruktor dla finalnych zmiennych
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final NotificationClient notificationClient;

    @Value("${server.port}")
    private String port;

    @Override
    public User save(User user) {
        roleRepository.findByName("USER").ifPresent(role -> user.setRoles(Collections.singleton(role)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var token = UUID.randomUUID().toString();
        user.setToken(token);
        var savedUser = userRepository.save(user);
        var variables = new HashMap<String, Object>();
        variables.put("url", "http://localhost:" + port +"/api/v1/users/confirm?token=" + token);
        notificationClient.sendMail(MailDto.builder()
                .receiveAddress(user.getEmail())
                .templateName("NEW_USER_WELCOME")
                .variables(variables)
                .build());
        return savedUser;
    }

    @Override
    @Transactional //dirty update
    public User update(User user, Long id) {
        var userDb = getUserById(id);
        userDb.setFirstName(user.getFirstName());
        userDb.setLastName(user.getLastName());
        userDb.setUsername(user.getUsername());
        userDb.setEmail(user.getEmail());
        return userDb;
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getReferenceById(id); //STARE getById .....
    }

    @Override
    public Page<User> getPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getCurrentUser() {
        return userRepository.findByEmail(SecurityUtils.getCurrentUserEmail()).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    @Transactional
    public void confirmUser(String token) {
        var user = userRepository.findByToken(token).orElseThrow(EntityNotFoundException::new);
        user.setToken(null);
    }


}
