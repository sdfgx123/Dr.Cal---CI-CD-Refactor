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

        public static AdminUserListDTO of(User user) {
            return new AdminUserListDTO(user.getId(), user.getName(), user.getPhone(), user.getHospital().getName(),
                    user.getDept().getName(), user.getLevel(), user.getAuth(), user.getStatus());
        }

        public static List<AdminUserListDTO> listOf(List<User> users){
            return users.stream().map(AdminUserListDTO::of).collect(Collectors.toList());
        }
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

        public static joinReqListDTO of(User user) {
            return new joinReqListDTO(user.getId(), user.getName(), user.getPhone(),
                    user.getHospital().getName(), user.getDept().getName(), user.getLevel(), user.getStatus());
        }

        public static List<joinReqListDTO> listOf(List<User> userList) {
            return userList.stream().map(joinReqListDTO::of).collect(Collectors.toList());
        }
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
        private LocalDate startDate;
        private LocalDate endDate;
        private EvaluationEnum evaluation;

        public static AdminAnnualListDTO of(Schedule schedule) {
            return new AdminAnnualListDTO(schedule.getId(), schedule.getUser().getName(),
                    schedule.getCategory(), schedule.getUser().getLevel(), schedule.getCreatedAt(),
                    schedule.getStartDate(), schedule.getEndDate(), schedule.getEvaluation());
        }

        public static List<AdminAnnualListDTO> listOf(List<Schedule> scheduleList){
            return scheduleList.stream().map(AdminAnnualListDTO::of).collect(Collectors.toList());
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DutyListDTO {
        private Long scheduleId;
        private String username;
        private CategoryEnum category;
        private LevelEnum level;
        private LocalDateTime createdAt;
        private LocalDate startDate;
        private EvaluationEnum evaluation;

        public static DutyListDTO of(Schedule schedule) {
            return new DutyListDTO(schedule.getId(), schedule.getUser().getName(),
                    schedule.getCategory(), schedule.getUser().getLevel(), schedule.getCreatedAt(),
                    schedule.getStartDate(), schedule.getEvaluation());
        }

        public static List<DutyListDTO> listOf(List<Schedule> scheduleList){
            return scheduleList.stream().map(DutyListDTO::of).collect(Collectors.toList());
        }
    }
}
