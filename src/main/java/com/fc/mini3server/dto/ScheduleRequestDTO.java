package com.fc.mini3server.dto;

import com.fc.mini3server.domain.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@RequiredArgsConstructor
public class ScheduleRequestDTO {

    @Data
    public static class createAnnualDTO {

        @NotNull
        private LocalDate startDate;

        @NotNull
        private LocalDate endDate;

        @NotNull
        private String reason;

    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class updateDutyDTO {
        private LocalDate startDate;
        private LocalDate updateDate;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class getScheduleReqDTO {
        private LocalDate chooseDate;
        private CategoryEnum category;
    }
}

