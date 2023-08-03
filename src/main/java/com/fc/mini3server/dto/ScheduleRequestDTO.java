package com.fc.mini3server.dto;

import com.fc.mini3server.domain.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class ScheduleRequestDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class registerAnnualDTO {
        private LocalDate startDate;
        private LocalDate endDate;
        private String reason;
    }
}
