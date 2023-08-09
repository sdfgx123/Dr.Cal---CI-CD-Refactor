package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server.domain.*;
import com.fc.mini3server._core.handler.exception.Exception404;
import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.EvaluationEnum;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.dto.ScheduleResponseDTO;
import com.fc.mini3server.repository.ScheduleRepository;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.fc.mini3server.dto.ScheduleRequestDTO.*;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<ScheduleResponseDTO.ApprovedScheduleListDTO> getApprovedSchedule() {
        Long hospitalId = userService.getUser().getHospital().getId();
        return ScheduleResponseDTO.ApprovedScheduleListDTO.listOf(scheduleRepository.findByEvaluationAndUserHospitalId(EvaluationEnum.APPROVED, hospitalId));
    }

    public Schedule createAnnualSchedule(createAnnualDTO createAnnualDTO) {
        try {
            User user = userRepository.findById(userService.getUser().getId())
                    .orElseThrow(() -> new Exception400("id: " + userService.getUser().getId(), Message.INVALID_ID_PARAMETER));

            List<EvaluationEnum> findEvaluations = Arrays.asList(EvaluationEnum.APPROVED, EvaluationEnum.STANDBY);

            if (!createAnnualDTO.getStartDate().isAfter(createAnnualDTO.getEndDate())) {
                if (scheduleRepository.existsByUserIdAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndEvaluationIn(
                        user.getId(), CategoryEnum.ANNUAL, createAnnualDTO.getEndDate(), createAnnualDTO.getStartDate(), findEvaluations)) {
                    throw new Exception400(Message.ALREADY_EXISTS_ON_THAT_DATE_ANNUAL);
                }

                long updateAnnual = ChronoUnit.DAYS.between(createAnnualDTO.getStartDate(), createAnnualDTO.getEndDate());

                if (user.getAnnual() > 0 && user.getAnnual() >= updateAnnual) {
                    Schedule schedule = Schedule.builder()
                            .user(user)
                            .hospital(user.getHospital())
                            .category(CategoryEnum.ANNUAL)
                            .startDate(createAnnualDTO.getStartDate())
                            .endDate(createAnnualDTO.getEndDate())
                            .evaluation(EvaluationEnum.STANDBY)
                            .reason(createAnnualDTO.getReason())
                            .build();

                    user.usedAnnual((int) updateAnnual);
                    userRepository.save(user);

                    return scheduleRepository.save(schedule);
                } else {
                    throw new Exception400(Message.NO_USER_ANNUAL_LEFT);
                }
            } else {
                throw new Exception400(Message.INVALID_DATE_RANGE);
            }

        } catch (IllegalArgumentException e) {
            throw new Exception400(Message.INVALID_CREATE_ANNUAL_FORMAT);
        }
    }

    @Transactional
    public Schedule updateAnnual(Long id, createAnnualDTO updateDTO) {
        try {
            Schedule schedule = scheduleRepository.findById(id)
                    .orElseThrow(() -> new Exception400(Message.INVALID_SCHEDULE_PARAMETER));
            User user = userService.getUser();
            List<EvaluationEnum> findEvaluations = Arrays.asList(EvaluationEnum.APPROVED, EvaluationEnum.STANDBY);

            if (schedule.getEvaluation() == EvaluationEnum.CANCELED) {
                throw new Exception400(Message.INVALID_EVALUATION_CANCELED);
            }

            if (updateDTO.getStartDate().isAfter(updateDTO.getEndDate())) {
                throw new Exception400(Message.INVALID_DATE_RANGE);
            }

            if (scheduleRepository.existsByUserIdAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndEvaluationIn(
                    user.getId(), CategoryEnum.ANNUAL, updateDTO.getEndDate(), updateDTO.getStartDate(), findEvaluations)) {
                throw new Exception400(Message.ALREADY_EXISTS_ON_THAT_DATE_ANNUAL);
            }

            long originalAnnual = ChronoUnit.DAYS.between(schedule.getStartDate(), schedule.getEndDate());
            user.setAnnual((int) (user.getAnnual() + (originalAnnual + 1)));

            schedule.setStartDate(updateDTO.getStartDate());
            schedule.setEndDate(updateDTO.getEndDate());
            schedule.setReason(updateDTO.getReason());
            schedule.setEvaluation(EvaluationEnum.STANDBY);

            long updatedAnnual = ChronoUnit.DAYS.between(schedule.getStartDate(), schedule.getEndDate());

            if (originalAnnual != updatedAnnual) {
                if (user.getAnnual()>= updatedAnnual && user.getAnnual() >= 0) {
                    user.usedAnnual((int) updatedAnnual);
                    userRepository.save(user);
                } else {
                    throw new Exception400(Message.NO_USER_ANNUAL_LEFT);
                }
            }

            return scheduleRepository.save(schedule);

        } catch (IllegalArgumentException e) {
            throw new Exception400(Message.INVALID_CREATE_ANNUAL_FORMAT);
        }
    }

    @Transactional
    public void changeReqDuty(Long id, updateDutyDTO requestDTO) {
        User user = userService.getUser();
      
        Schedule originSchedule = scheduleRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER)
        );

        if (scheduleRepository.existsByHospitalIdAndEvaluationAndCategoryAndEndDate(
                user.getHospital().getId(), EvaluationEnum.STANDBY, CategoryEnum.DUTY, requestDTO.getUpdateDate()))
            throw new Exception400(Message.ALREADY_EXISTS_CHANGE_DUTY_REQUEST);

        scheduleRepository.findByHospitalIdAndEvaluationAndCategoryAndStartDate(
                user.getHospital().getId(), EvaluationEnum.APPROVED, CategoryEnum.DUTY, requestDTO.getUpdateDate())
                .orElseThrow(
                        () -> new Exception400(Message.NO_DUTY_SCHEDULE_ON_DATE)
                );

        Schedule schedule = Schedule.builder()
                .user(user)
                .hospital(user.getHospital())
                .category(CategoryEnum.DUTY)
                .startDate(originSchedule.getStartDate())
                .endDate(requestDTO.getUpdateDate())
                .evaluation(EvaluationEnum.STANDBY)
                .build();

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new Exception400(Message.INVALID_SCHEDULE_PARAMETER));

        User user = userService.getUser();

        if (!schedule.getCategory().equals(CategoryEnum.ANNUAL)) {
            throw new Exception400(Message.INVALID_SCHEDULE_CATEGORY_NOT_ANNUAL);
        }

        if (EvaluationEnum.CANCELED.equals(schedule.getEvaluation())) {
            throw new Exception400(Message.ALREADY_EXISTS_CANCELED_ANNUAL);
        }

        long originalAnnual = ChronoUnit.DAYS.between(schedule.getStartDate(), schedule.getEndDate());
        user.setAnnual((int) (user.getAnnual() + (originalAnnual + 1)));
        userRepository.save(user);

        scheduleRepository.updateEvaluationToCanceled(id);
    }

    public List<Schedule> findAllScheduleListByDate(getScheduleReqDTO requestDTO){
        Long hospitalId = userService.getUser().getHospital().getId();

        return scheduleRepository.findByHospitalIdAndEvaluationAndCategoryAndStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual(
                hospitalId, EvaluationEnum.APPROVED, requestDTO.getCategory(), requestDTO.getChooseDate(), requestDTO.getChooseDate());
    }

    public Schedule findByDutyScheduleByDate(getScheduleReqDTO requestDTO){
        Long hospitalId = userService.getUser().getHospital().getId();

        return scheduleRepository.findByHospitalIdAndEvaluationAndCategoryAndStartDateAndEndDate(
                hospitalId, EvaluationEnum.APPROVED, requestDTO.getCategory(), requestDTO.getChooseDate(), requestDTO.getChooseDate()
        ).orElseThrow(
                () -> new Exception404("금일 당직 인원이 없습니다.")
        );
    }

    public List<Schedule> findAllRequestSchedule (Long id){
        userRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER)
        );

        return scheduleRepository.findByUserId(id);
    }
}
