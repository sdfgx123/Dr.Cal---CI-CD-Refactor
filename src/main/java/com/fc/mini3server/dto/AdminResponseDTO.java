package com.fc.mini3server.dto;

import com.fc.mini3server.domain.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AdminResponseDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AdminUserListDTO {
        private Long id;
        private String username;
        private String phone;
        private String hospitalName;
        private String deptName;
        private LevelEnum level;
        private AuthEnum auth;
        private StatusEnum status;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class joinReqListDTO {
        private Long id;
        private String username;
        private String phone;
        private String hospitalName;
        private String deptName;
        private LevelEnum level;
        private StatusEnum status;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AdminAnnualListDTO {
        private Long scheduleId;
        private String username;
        private CategoryEnum category;
        private LevelEnum level;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDate startDate;
        private LocalDate endDate;
        private EvaluationEnum evaluation;

        public static AdminAnnualListDTO of(Schedule schedule) {
            return new AdminAnnualListDTO(schedule.getId(), schedule.getUser().getName(),
                    schedule.getCategory(), schedule.getUser().getLevel(), schedule.getCreatedAt(), schedule.getUpdatedAt(),
                    schedule.getStartDate(), schedule.getEndDate(), schedule.getEvaluation());
        }

        public static List<AdminAnnualListDTO> listOf(List<Schedule> scheduleList) {
            return scheduleList.stream().map(AdminAnnualListDTO::of).collect(Collectors.toList());
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DutyListDTO {
        private Long scheduleId;
        private String username;
        private String hospitalName;
        private CategoryEnum category;
        private LevelEnum level;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDate startDate;
        private LocalDate updateDate;
        private EvaluationEnum evaluation;

        public static DutyListDTO of(Schedule schedule) {
            return new DutyListDTO(schedule.getId(), schedule.getUser().getName(), schedule.getHospital().getName(),
                    schedule.getCategory(), schedule.getUser().getLevel(), schedule.getCreatedAt(), schedule.getUpdatedAt(),
                    schedule.getStartDate(), schedule.getEndDate(), schedule.getEvaluation());
        }

        public static List<DutyListDTO> listOf(List<Schedule> scheduleList) {
            return scheduleList.stream().map(DutyListDTO::of).collect(Collectors.toList());
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UserListByHospitalIdDTO {
        private Long userId;
        private String username;
        private String hospitalName;
        private String deptName;
        private LevelEnum level;
        private int duty;

        public static UserListByHospitalIdDTO of(User user) {
            return new UserListByHospitalIdDTO(user.getId(), user.getName(), user.getHospital().getName(),
                    user.getDept().getName(), user.getLevel(), user.getDuty());
        }

        public static List<UserListByHospitalIdDTO> listOf(List<User> userList) {
            return userList.stream().map(UserListByHospitalIdDTO::of).collect(Collectors.toList());
        }


    }
}
