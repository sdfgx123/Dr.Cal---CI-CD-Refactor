package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.dto.UserRequestDTO;
import com.fc.mini3server.dto.UserResponseDTO;
import com.fc.mini3server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiUtils.ApiResult<User>> register(@RequestBody @Valid UserRequestDTO.registerDTO registerDTO, Errors errors) {
        userService.registerNewUser(registerDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

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
