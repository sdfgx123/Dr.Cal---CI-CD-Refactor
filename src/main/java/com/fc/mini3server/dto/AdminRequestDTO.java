package com.fc.mini3server.dto;

import com.fc.mini3server.domain.AuthEnum;
import com.fc.mini3server.domain.EvaluationEnum;
import com.fc.mini3server.domain.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class AdminRequestDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class editAuthDTO {
        private AuthEnum auth;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class editStatusDTO {
        private StatusEnum status;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class editEvaluationDTO {
        private EvaluationEnum evaluation;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class createDutyAdminDTO {
        private LocalDate chooseDate;
    }
}
