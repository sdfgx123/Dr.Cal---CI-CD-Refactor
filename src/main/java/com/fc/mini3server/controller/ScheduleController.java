package com.fc.mini3server.controller;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.service.ScheduleService;
import com.fc.mini3server.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static com.fc.mini3server.dto.ScheduleRequestDTO.*;
import static com.fc.mini3server.dto.ScheduleResponseDTO.*;

@Tag(name = "Schedule", description = "캘린더 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;

    @Operation(summary = "메인 캘린더 조회", description = "evaluation = APPROVED 인 건만 조회")
    @GetMapping("/")
    public ResponseEntity<?> getApprovedSchedules() {
        return ResponseEntity.ok(ApiUtils.success(scheduleService.getApprovedSchedule()));
    }

    @Operation(summary = "날짜별 휴가/당직 인원 조회", description = "날짜별로 클릭 시 데이터 출력")
    @GetMapping("/date")
    public ResponseEntity<?> getScheduleByDate(@RequestParam("chooseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate chooseDate,
                                                 @RequestParam("category") CategoryEnum category){
        getScheduleReqDTO requestDTO = new getScheduleReqDTO(chooseDate, category);

        if(category.equals(CategoryEnum.ANNUAL)){
            List<Schedule> scheduleList = scheduleService.findAllScheduleListByDate(requestDTO);
            return ResponseEntity.ok(ApiUtils.success(annualListByDateDTO.listOf(scheduleList)));
        }

        if(category.equals(CategoryEnum.DUTY)){
            Schedule schedule = scheduleService.findByDutyScheduleByDate(requestDTO);
            return ResponseEntity.ok(ApiUtils.success(dutyScheduleDTO.of(schedule)));
        }

        return ResponseEntity.ok(ApiUtils.error(Message.METHOD_ARGUMENT_TYPE_MISMATCH, HttpStatus.BAD_REQUEST));
    }

    @Operation(summary = "요청 내역 확인", description = "파라미터 userId")
    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleRequestList(@PathVariable Long id){
        List<Schedule> scheduleList = scheduleService.findAllRequestSchedule(id);
        return ResponseEntity.ok(ApiUtils.success(ScheduleReqListDTO.listOf(scheduleList)));
    }

    @Operation(summary = "연차 변경 신청")
    @PostMapping("annual/{id}/update")
    public ResponseEntity<?> updateAnnualSchedule(@PathVariable Long id, @RequestBody createAnnualDTO updateDTO) {
        scheduleService.updateAnnual(id, updateDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "당직 변경 신청", description = "현재 본인의 스케줄에서 변경 요청을 하는 스케줄의 Date를 넣는다.")
    @PostMapping("duty/{scheduleId}/update")
    public ResponseEntity<?> changeDutySchedule(@PathVariable Long scheduleId, @RequestBody updateDutyDTO requestDTO) {
        scheduleService.changeReqDuty(scheduleId, requestDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "연차 등록")
    @PostMapping("/create/annual")
    public ResponseEntity<ApiUtils.ApiResult<Schedule>> createAnnualSchedule(@RequestBody @Valid createAnnualDTO createAnnualDTO) {
        scheduleService.createAnnualSchedule(createAnnualDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @Operation(summary = "연차 삭제")
    @PostMapping("/annual/delete")
    public ResponseEntity<?> deleteAnnualSchedule(@RequestParam Long id) {
        scheduleService.deleteAnnual(id);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/on")
    public ResponseEntity<ApiUtils.ApiResult<String>> startWork() {
        Long userId = userService.getUser().getId();
        scheduleService.startWork(userId);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/off")
    public ResponseEntity<ApiUtils.ApiResult<String>> endWork() {
        Long userId = userService.getUser().getId();
        scheduleService.endWork(userId);
        return ResponseEntity.ok(ApiUtils.success(null));
    }
}
