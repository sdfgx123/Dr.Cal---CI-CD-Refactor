package com.fc.mini3server.controller;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.dto.ScheduleRequestDTO;
import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.service.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/")
    public ResponseEntity<?> getApprovedSchedules(Pageable pageable) {
        return ResponseEntity.ok(ApiUtils.success(scheduleService.getApprovedSchedule(pageable).getContent()));
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleRequestList(@PathVariable Long id){
        List<Schedule> scheduleList = scheduleService.findAllRequestSchedule(id);
        return ResponseEntity.ok(ApiUtils.success(ScheduleReqListDTO.listOf(scheduleList)));
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
