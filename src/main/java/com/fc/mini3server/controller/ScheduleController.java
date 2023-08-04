package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.dto.ScheduleResponseDTO;
import com.fc.mini3server.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.fc.mini3server.dto.ScheduleRequestDTO.*;

@Tag(name = "Schedule", description = "캘린더 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/")
    public ResponseEntity<?> getApprovedSchedules(Pageable pageable) {
        return ResponseEntity.ok(ApiUtils.success(scheduleService.getApprovedSchedule(pageable).getContent()));
    }

    @GetMapping("/date")
    public ResponseEntity<?> getAnnualListByDate(@RequestParam("chooseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate chooseDate,
                                                 @RequestParam("category") CategoryEnum category){
        List<Schedule> scheduleList = scheduleService.findAllScheduleListByDate(new annualListReqDTO(chooseDate, category));
        return ResponseEntity.ok(ApiUtils.success(ScheduleResponseDTO.annualListByDateDTO.listOf(scheduleList)));
    }
}
