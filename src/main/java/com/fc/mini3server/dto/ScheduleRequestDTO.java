package com.fc.mini3server.dto;

import com.fc.mini3server.domain.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class ScheduleRequestDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class annualListReqDTO {
        private LocalDate chooseDate;
        private CategoryEnum category;
    }
}
