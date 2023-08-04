package com.fc.mini3server.dto;

import com.fc.mini3server.domain.*;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
public class ScheduleRequestDTO {

    @Data
    public static class createAnnualDTO {

        @NotNull
        private LocalDate startDate;

        @NotNull
        private LocalDate endDate;

        @NotNull
        private String reason;

        public Schedule toEntity(User user, Hospital hospital) {
            return Schedule.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .reason(reason)
                    .category(CategoryEnum.ANNUAL)
                    .user(user)
                    .hospital(hospital)
                    .evaluation(EvaluationEnum.STANDBY)
                    .build();
        }

    }

}
