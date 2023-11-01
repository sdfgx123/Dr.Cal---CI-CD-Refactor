package com.fc.mini3server.controller;

import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.domain.Work;
import com.fc.mini3server.dto.UserRequestDTO;
import com.fc.mini3server.dto.UserResponseDTO;
import com.fc.mini3server.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Tag(name = "User", description = "유저 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiUtils.ApiResult<User>> register(@RequestBody @Valid UserRequestDTO.registerDTO registerDTO, Errors errors) {
        validateDTO(errors);
        userService.registerNewUser(registerDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiUtils.ApiResult<String>> login(@RequestBody @Valid UserRequestDTO.loginDTO loginDTO, Errors errors) {
        validateDTO(errors);
        String jwtToken = userService.login(loginDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, jwtToken);
        log.info("JWT 토큰 발급 완료 | 발급 대상 유저 이메일 : " + loginDTO.getEmail());
        return ResponseEntity.ok().headers(headers).body(ApiUtils.success(null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiUtils.ApiResult<String>> logout() {
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/updatePassword")
    public ResponseEntity<ApiUtils.ApiResult<String>> updatePassword(@RequestBody @Valid UserRequestDTO.updatePasswordDTO updatePasswordDTO, Errors errors) {
        validateDTO(errors);
        userService.updatePasswordProc(updatePasswordDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @GetMapping("/myPage")
    public ResponseEntity<ApiUtils.ApiResult<UserResponseDTO.MyPageDTO>> myPage() {
        User user = userService.getUser();
        Work work = userService.getWorkInfoWithUser(user);
        UserResponseDTO.MyPageDTO myPageDTO = UserResponseDTO.MyPageDTO.of(user, work);
        log.info("유저 상세정보 호출 | 호출 대상 유저 : " + user.getEmail());
        return ResponseEntity.ok(ApiUtils.success(myPageDTO));
    }

    @GetMapping("/myPage/work")
    public ResponseEntity<ApiUtils.ApiResult<UserResponseDTO.MyPageWorkDTO>> myPageWork(Pageable pageable) {
        User user = userService.getUser();
        UserResponseDTO.MyPageWorkDTO myPageWorkDTO = userService.getMyPageWork(user, pageable);
        return ResponseEntity.ok(ApiUtils.success(myPageWorkDTO));
    }

    @PostMapping("/editUser")
    public ResponseEntity<ApiUtils.ApiResult<String>> updateUser(@RequestBody @Valid UserRequestDTO.updateUserDTO updateUserDTO, Errors errors) {
        validateDTO(errors);
        userService.updateUserProc(updateUserDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    public void validateDTO(Errors errors) {
        if (errors.hasErrors()) {
            String msg = errors.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            throw new Exception400(msg);
        }
    }

}
