package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final AdminService adminService;

    @Operation(summary = "사용자 관리", description = "디폴트: 최신순(createdAt) + 퇴사 state를 가진 User는 가장 뒤로 정렬")
    @GetMapping("/users")
    public ResponseEntity<?> findAllUsers(@PageableDefault(size = 10) Pageable pageable) {
        final Page<AdminUserListDTO> userList = adminService.findAllUserListAdmin(pageable);
        return ResponseEntity.ok(ApiUtils.success(
                userList.getTotalPages(), userList.getContent())
        );
    }

    @Operation(summary = "회원 가입 요청 페이지", description = "디폴트: 최신순(createdAt)")
    @GetMapping("/register")
    public ResponseEntity<?> joinUserList(@PageableDefault(size = 10) Pageable pageable) {
        final Page<joinReqListDTO> joinList = adminService.findAllJoinUserListAdmin(pageable);
        return ResponseEntity.ok(ApiUtils.success(
                joinList.getTotalPages(), joinList.getContent())
        );
    }

    @Operation(summary = "유저 권한 변경", description = "USER -> ADMIN, ADMIN -> USER")
    @PostMapping("/users/{id}/auth")
    public ResponseEntity<?> editAuth(@PathVariable Long id, @RequestBody editAuthDTO requestDTO) {
        adminService.updateUserAuth(id, requestDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "유저 권한 승인", description = "NOTAPPROVED -> APPROVED")
    @PostMapping("/users/{id}/approve")
    public ResponseEntity<?> userApprove(@PathVariable Long id) {
        adminService.approveUser(id);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "유저 재직 상태 변경", description = "APPROVED -> RETIRED")
    @PostMapping("/users/{id}/retire")
    public ResponseEntity<?> userRetire(@PathVariable Long id) {
        adminService.retireUser(id);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "연차 신청 관리", description = "디폴트: 오래된순 + state 처리 완료된건 뒤로 정렬")
    @GetMapping("/annual")
    public ResponseEntity<?> findAnnualList(@PageableDefault(size = 10) Pageable pageable) {
        final Page<Schedule> scheduleList = adminService.findAnnualList(pageable);
        return ResponseEntity.ok(ApiUtils.success(
                scheduleList.getTotalPages(),
                AdminAnnualListDTO.listOf(scheduleList.getContent()))
        );
    }

    @Operation(summary = "당직 변경 관리", description = "디폴트: 오래된순 + state 처리 완료된건 뒤로 정렬")
    @GetMapping("/duty")
    public ResponseEntity<?> findDutyList(@PageableDefault(size = 10) Pageable pageable) {
        final Page<Schedule> scheduleList = adminService.findDutyList(pageable);
        return ResponseEntity.ok(ApiUtils.success(
                scheduleList.getTotalPages(),
                DutyListDTO.listOf(scheduleList.getContent())));
    }

    @Operation(summary = "스케줄 승인/반려", description = "STANDBY -> APPROVED, STANDBY -> REJECTED")
    @PostMapping("/{scheduleId}/evaluation")
    public ResponseEntity<?> editEvaluation(@PathVariable Long scheduleId, @RequestBody editEvaluationDTO requestDTO) {
        adminService.updateScheduleEvaluation(scheduleId, requestDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "병원 별 의사 목록", description = "병원Id를 가지고 당직등록을 위한 사람을 찾는다.")
    @GetMapping("/hospitalUsers")
    public ResponseEntity<?> findUserListByHospital(@RequestParam(required = false, defaultValue = "") List<LevelEnum> levelList) {
        List<User> userList = adminService.findAllUserListByHospitalIdAdmin(levelList);
        return ResponseEntity.ok(ApiUtils.success(UserListByHospitalIdDTO.listOf(userList)));
    }

    @Operation(summary = "당직 추가", description = "선택한 당직인원을 실제 당직인원으로 추가한다.")
    @PostMapping("/{userId}/createDuty")
    public ResponseEntity<?> createDuty(@PathVariable Long userId, @RequestBody createDutyAdminDTO requestDTO) {
        adminService.createDuty(userId, requestDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "당직 삭제", description = "요청한 날짜에 당직을 삭제한다.")
    @PostMapping("/{scheduleId}/deleteDuty")
    public ResponseEntity<?> deleteDuty(@PathVariable Long scheduleId) {
        adminService.deleteDuty(scheduleId);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @GetMapping("/work/dashboard")
    public ResponseEntity<?> workDashboard(
            @RequestParam(name = "level", required = false, defaultValue = "") LevelEnum level,
            @RequestParam("dept") String dept, @PageableDefault(size = 10) Pageable pageable){
        // TODO: Spring Batch로 데이터 가공 후 가져오기
        return null;
    }

    @GetMapping("/work")
    public ResponseEntity<?> work(
            @RequestParam(name = "level", required = false, defaultValue = "") LevelEnum level,
            @RequestParam("dept") String dept, @PageableDefault(size = 10) Pageable pageable){
        final Page<UserWorkListDTO> userWorkList = adminService.findUserWorkList(level, dept, pageable);
        return ResponseEntity.ok(ApiUtils.success(
                userWorkList.getTotalPages(), userWorkList.getContent()
        ));
    }
}
