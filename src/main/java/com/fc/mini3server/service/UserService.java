package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.dto.UserRequestDTO;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerNewUser(UserRequestDTO.registerDTO registerDTO) {
        userRepository.save(registerDTO.toEntity(passwordEncoder));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception400(String.valueOf(id) ,"해당 아이디가 존재하지 않습니다."));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
