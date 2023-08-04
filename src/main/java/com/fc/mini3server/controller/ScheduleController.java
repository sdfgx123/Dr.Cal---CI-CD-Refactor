package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.dto.ScheduleRequestDTO;
import com.fc.mini3server.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> createAnnualSchedule(@RequestBody ScheduleRequestDTO.createAnnualDTO createAnnualDTO, @AuthenticationPrincipal User user, Hospital hospital) {
        Schedule createdSchedule = scheduleService.createSchedule(createAnnualDTO, user, hospital);
        return ResponseEntity.ok(ApiUtils.success(null));
    }
}
