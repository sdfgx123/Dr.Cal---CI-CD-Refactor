package com.fc.mini3server.dto;

import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.EvaluationEnum;
import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.Schedule;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ScheduleResponseDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ApprovedScheduleListDTO {
        private Long id;
        private String name;
        private LevelEnum level;
        private String hospitalName;
        private String deptName;
        private CategoryEnum category;
        private LocalDate startDate;
        private LocalDate endDate;
        private EvaluationEnum evaluation;


        public static ApprovedScheduleListDTO of(Schedule schedule) {
            ApprovedScheduleListDTO approvedScheduleListDTO = new ApprovedScheduleListDTO(
                    schedule.getId(),
                    schedule.getUser().getName(),
                    schedule.getUser().getLevel(),
                    schedule.getUser().getHospital().getName(),
                    schedule.getUser().getDept().getName(),
                    schedule.getCategory(), schedule.getStartDate(), schedule.getEndDate(), schedule.getEvaluation());
            return approvedScheduleListDTO;
        }

        public static List<ApprovedScheduleListDTO> listOf(List<Schedule> scheduleList) {
            return scheduleList.stream().map(ApprovedScheduleListDTO::of).collect(Collectors.toList());
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class annualListByDateDTO {
        private Long id;
        private String username;
        private String HospitalName;
        private String deptName;
        private LevelEnum level;
        private String phone;

        public static annualListByDateDTO of(Schedule schedule){
            return new annualListByDateDTO(schedule.getId(), schedule.getUser().getName(), schedule.getHospital().getName(),
                    schedule.getUser().getDept().getName(), schedule.getUser().getLevel(), schedule.getUser().getPhone());
        }

        public static List<annualListByDateDTO> listOf(List<Schedule> scheduleList) {
            return scheduleList.stream().map(annualListByDateDTO::of).collect(Collectors.toList());
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class dutyScheduleDTO {
        private Long id;
        private Long userId;
        private String username;
        private String hospitalName;
        private String deptName;
        private LevelEnum level;
        private String phone;
        private String email;
        private String profileImageUrl;

        public static dutyScheduleDTO of(Schedule schedule){
            return new dutyScheduleDTO(schedule.getId(), schedule.getUser().getId(), schedule.getUser().getName(), schedule.getHospital().getName(),
                    schedule.getUser().getDept().getName(), schedule.getUser().getLevel(), schedule.getUser().getPhone(),
                    schedule.getUser().getEmail(), schedule.getUser().getProfileImageUrl());
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class ScheduleReqListDTO {
        private Long id;
        private Long userId;
        private Long hospitalId;
        private CategoryEnum category;
        private LocalDate startDate;
        private LocalDate endDate;
        private EvaluationEnum evaluation;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ScheduleReqListDTO of(Schedule schedule){
            return new ScheduleReqListDTO(schedule.getId(), schedule.getUser().getId(), schedule.getHospital().getId(),
                    schedule.getCategory(), schedule.getStartDate(), schedule.getEndDate(),
                    schedule.getEvaluation(), schedule.getCreatedAt(), schedule.getUpdatedAt()
                    );
        }

        public static List<ScheduleReqListDTO> listOf(List<Schedule> scheduleList) {
            return scheduleList.stream().map(ScheduleReqListDTO::of).collect(Collectors.toList());
        }
    }
}
