package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.dto.ScheduleRequestDTO;
import com.fc.mini3server.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Schedule", description = "캘린더 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/")
    public ResponseEntity<?> getApprovedSchedules(Pageable pageable) {
        return ResponseEntity.ok(ApiUtils.success(scheduleService.getApprovedSchedule(pageable)));
    }

    @PostMapping("/create/annual")
    public ResponseEntity<ApiUtils.ApiResult<Schedule>> createAnnualSchedule(@RequestBody @Valid ScheduleRequestDTO.createAnnualDTO createAnnualDTO) {
        scheduleService.createAnnualSchedule(createAnnualDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/create/duty")
    public ResponseEntity<ApiUtils.ApiResult<Schedule>> createDutySchedule(@RequestBody @Valid ScheduleRequestDTO.createDutyDTO createDutyDTO) {
        scheduleService.createDutySchedule(createDutyDTO);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

    @PostMapping("/annual/delete")
    public ResponseEntity<?> deleteAnnualSchedule(@RequestParam Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(ApiUtils.success(null));
    }

}
