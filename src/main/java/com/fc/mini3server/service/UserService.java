package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception400(String.valueOf(id) ,"해당 아이디가 존재하지 않습니다."));
        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
