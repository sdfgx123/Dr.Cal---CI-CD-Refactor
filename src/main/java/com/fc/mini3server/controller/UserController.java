package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.dto.UserRequestDTO;
import com.fc.mini3server.dto.UserResponseDTO;
import com.fc.mini3server.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "User", description = "유저 API")
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

    @PostMapping("/login")
    public ResponseEntity<ApiUtils.ApiResult<String>> login(@RequestBody @Valid UserRequestDTO.loginDTO loginDTO, Errors errors) {
        String jwtToken = userService.login(loginDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, jwtToken);

        log.info("JWT Token provided : " + jwtToken);

        return ResponseEntity.ok().headers(headers).body(null);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiUtils.ApiResult<String>> logout() {
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<ApiUtils.ApiResult<String>> updatePassword(@RequestBody @Valid UserRequestDTO.updatePasswordDTO updatePasswordDTO, Errors errors) {
        userService.updatePasswordProc(updatePasswordDTO);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/myPage")
    public ResponseEntity<ApiUtils.ApiResult<UserResponseDTO>> myPage() {
        User user = userService.getUser();
        UserResponseDTO responseDTO = UserResponseDTO.of(user);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }

    @PostMapping("/editUser")
    public ResponseEntity<ApiUtils.ApiResult<String>> updateUser(@RequestBody @Valid UserRequestDTO.updateUserDTO updateUserDTO, Errors errors) {
        userService.updateUserProc(updateUserDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        final User user = userService.findById(id);
        return ResponseEntity.ok(ApiUtils.success(UserResponseDTO.of((user))));
    }

//    @GetMapping("/users")
//    public ResponseEntity<?> findAll(){
//        final List<User> allUsers = userService.findAll();
//        return ResponseEntity.ok(ApiUtils.success(UserResponseDTO.listOf(allUsers)));
//    }
}
