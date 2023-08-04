package com.fc.mini3server.dto;

import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.EvaluationEnum;
import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
        private Long hospitalId;
        private CategoryEnum category;
        private LocalDate startDate;
        private LocalDate endDate;
        private EvaluationEnum evaluation;


        public static ApprovedScheduleListDTO of(Schedule schedule) {
            ApprovedScheduleListDTO approvedScheduleListDTO = new ApprovedScheduleListDTO(
                    schedule.getId(),
                    schedule.getUser().getName(),
                    schedule.getUser().getLevel(),
                    schedule.getUser().getHospital().getId(),
                    schedule.getCategory(), schedule.getStartDate(), schedule.getEndDate(), schedule.getEvaluation());
            return approvedScheduleListDTO;
        }

        public static List<ApprovedScheduleListDTO> listOf(List<Schedule> scheduleList) {
            return scheduleList.stream().map(ApprovedScheduleListDTO::of).collect(Collectors.toList());
        }
    }
}
