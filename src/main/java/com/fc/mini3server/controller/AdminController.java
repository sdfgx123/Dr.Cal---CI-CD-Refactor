package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.service.ScheduleService;
import com.fc.mini3server.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.fc.mini3server.dto.AdminRequestDTO.*;
import static com.fc.mini3server.dto.AdminResponseDTO.*;

@Tag(name = "Admin", description = "어드민 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final ScheduleService scheduleService;

    @GetMapping("/users")
    public ResponseEntity<?> findAllUsers(Pageable pageable) {
        final Page<User> userList = userService.findAllUserListAdmin(pageable);
        return ResponseEntity.ok(ApiUtils.success(userList.getTotalPages(),
                AdminUserListDTO.listOf(userList.getContent())));
    }

    @GetMapping("/register")
    public ResponseEntity<?> joinUserList(Pageable pageable) {
        final Page<User> joinList = userService.findAllJoinUserListAdmin(pageable);
        return ResponseEntity.ok(ApiUtils.success(joinList.getTotalPages(),
                joinReqListDTO.listOf(joinList.getContent())));
    }

    @PostMapping("/users/{id}/auth")
    public ResponseEntity<?> editAuth(@PathVariable Long id, @RequestBody editAuthDTO requestDTO){
        userService.updateUserAuth(id, requestDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/users/{id}/status")
    public ResponseEntity<?> editStatus(@PathVariable Long id, @RequestBody editStatusDTO requestDTO){
        userService.updateUserStatus(id, requestDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @GetMapping("/annual")
    public ResponseEntity<?> findAnnualList(Pageable pageable){
        final List<Schedule> scheduleList = scheduleService.findAnnualList(pageable).getContent();
        return ResponseEntity.ok(ApiUtils.success(AdminAnnualListDTO.listOf(scheduleList)));
    }

    @GetMapping("/duty")
    public ResponseEntity<?> findDutyList(Pageable pageable){
        final List<Schedule> scheduleList = scheduleService.findDutyList(pageable).getContent();
        return ResponseEntity.ok(ApiUtils.success(DutyListDTO.listOf(scheduleList)));
    }

    @PostMapping("/{id}/evaluation")
    public ResponseEntity<?> editEvaluation(@PathVariable Long id, @RequestBody editEvaluationDTO requestDTO){
        scheduleService.updateScheduleEvaluation(id, requestDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }
}
