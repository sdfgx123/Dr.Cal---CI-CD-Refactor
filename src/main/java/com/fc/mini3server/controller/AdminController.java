package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.dto.AdminResponseDTO;
import com.fc.mini3server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> findAll(Pageable pageable) {
        final List<User> userList = userService.findAll(pageable).getContent();
        return ResponseEntity.ok(ApiUtils.success(AdminResponseDTO.AdminUserListDTO.listOf(userList)));
    }
}
