package com.fc.mini3server.dto;

import com.fc.mini3server.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class findUserWorkTimeDTO {
        private Long userId;
        private String username;
        private Long deptId;
        private LevelEnum level;
        private String todayWorkTime;
        private String weekWorkTime;
        private String monthWorkTime;
        private WorkStatusEnum status;
        private int totalPages;
    }

}
