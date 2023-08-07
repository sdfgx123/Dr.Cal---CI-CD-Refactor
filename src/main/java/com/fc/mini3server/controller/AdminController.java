package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.service.ScheduleService;
import com.fc.mini3server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.fc.mini3server.dto.AdminRequestDTO.*;
import static com.fc.mini3server.dto.AdminResponseDTO.*;

@Tag(name = "Admin", description = "어드민 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final ScheduleService scheduleService;

    @Operation(summary = "사용자 관리", description = "디폴트: 최신순(createdAt) + 퇴사 state를 가진 User는 가장 뒤로 정렬")
    @GetMapping("/users")
    public ResponseEntity<?> findAllUsers(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        final Page<User> userList = userService.findAllUserListAdmin(pageable);
        return ResponseEntity.ok(ApiUtils.success(
                userList.getTotalPages(),
                AdminUserListDTO.listOf(userList.getContent()))
        );
    }

    @Operation(summary = "회원 가입 요청", description = "디폴트: 최신순(createdAt)")
    @GetMapping("/register")
    public ResponseEntity<?> joinUserList(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        final Page<User> joinList = userService.findAllJoinUserListAdmin(pageable);
        return ResponseEntity.ok(ApiUtils.success(
                joinList.getTotalPages(),
                joinReqListDTO.listOf(joinList.getContent()))
        );
    }

    @Operation(summary = "유저 권한 변경", description = "USER -> ADMIN, ADMIN -> USER")
    @PostMapping("/users/{id}/auth")
    public ResponseEntity<?> editAuth(@PathVariable Long id, @RequestBody editAuthDTO requestDTO){
        userService.updateUserAuth(id, requestDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "유저 권한 승인", description = "NOTAPPROVED -> APPROVED")
    @PostMapping("/users/{id}/approve")
    public ResponseEntity<?> userApprove(@PathVariable Long id){
        userService.approveUser(id);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "유저 재직 상태 변경", description = "APPROVED -> RETIRED")
    @PostMapping("/users/{id}/retire")
    public ResponseEntity<?> userRetire(@PathVariable Long id){
        userService.retireUser(id);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "연차 신청 관리", description = "디폴트: 오래된순 + state 처리 완료된건 뒤로 정렬")
    @GetMapping("/annual")
    public ResponseEntity<?> findAnnualList(@PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) Pageable pageable){
        final Page<Schedule> scheduleList = scheduleService.findAnnualList(pageable);
        return ResponseEntity.ok(ApiUtils.success(
                scheduleList.getTotalPages(),
                AdminAnnualListDTO.listOf(scheduleList.getContent()))
        );
    }

    @Operation(summary = "당직 변경 관리", description = "디폴트: 오래된순 + state 처리 완료된건 뒤로 정렬")
    @GetMapping("/duty")
    public ResponseEntity<?> findDutyList(@PageableDefault(size = 10)
              @SortDefault.SortDefaults({
                      @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC),
                      @SortDefault(sort = "id", direction = Sort.Direction.DESC)
              }) Pageable pageable){
        final Page<Schedule> scheduleList = scheduleService.findDutyList(pageable);
        return ResponseEntity.ok(ApiUtils.success(
                scheduleList.getTotalPages(),
                DutyListDTO.listOf(scheduleList.getContent())));
    }

    @Operation(summary = "스케줄 승인/반려", description = "STANDBY -> APPROVED, STANDBY -> REJECTED")
    @PostMapping("/{id}/evaluation")
    public ResponseEntity<?> editEvaluation(@PathVariable Long id, @RequestBody editEvaluationDTO requestDTO){
        scheduleService.updateScheduleEvaluation(id, requestDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }
}
