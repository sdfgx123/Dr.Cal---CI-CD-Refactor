package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.dto.UserResponseDTO;
import com.fc.mini3server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/users/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        final User user = userService.findById(id);
        return ResponseEntity.ok(ApiUtils.success(UserResponseDTO.of((user))));
    }

    @GetMapping("/users")
    public ResponseEntity<?> findAll(){
        final List<User> allUsers = userService.findAll();
        return ResponseEntity.ok(ApiUtils.success(UserResponseDTO.listOf(allUsers)));
    }
}
