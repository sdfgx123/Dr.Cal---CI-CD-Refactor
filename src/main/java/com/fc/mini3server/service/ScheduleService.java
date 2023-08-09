package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server._core.handler.exception.Exception403;
import com.fc.mini3server.domain.*;
import com.fc.mini3server._core.handler.exception.Exception404;
import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.EvaluationEnum;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.dto.ScheduleRequestDTO;
import com.fc.mini3server.dto.ScheduleResponseDTO;
import com.fc.mini3server.repository.ScheduleRepository;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
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

    public Schedule createAnnualSchedule(ScheduleRequestDTO.createAnnualDTO createAnnualDTO) {
        try {
            User user = userRepository.findById(userService.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("invalid user id : " + userService.getUser().getId()));

            long updateAnnual = ChronoUnit.DAYS.between(createAnnualDTO.getStartDate(), createAnnualDTO.getEndDate());

            if (user.getAnnual() >= updateAnnual) {
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
                throw new Exception400("사용 가능 연차가 부족합니다.");
            }
        } catch (IllegalArgumentException e) {
            throw new Exception400("요청 형식이 잘못 되었습니다. 시작일, 종료일, 사유를 모두 입력 하였는지 확인하십시오.");
        }
    }

    public Schedule createDutySchedule(ScheduleRequestDTO.createDutyDTO createDutyDTO) {
        try {

            User user = userRepository.findById(userService.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("invalid user id : " + createDutyDTO.getUser().getId()));

            Schedule schedule = Schedule.builder()
                    .user(user)
                    .hospital(user.getHospital())
                    .category(CategoryEnum.DUTY)
                    .startDate(createDutyDTO.getStartDate())
                    .endDate(createDutyDTO.getStartDate())
                    .evaluation(EvaluationEnum.STANDBY)
                    .reason("당직")
                    .build();

            return scheduleRepository.save(schedule);

        } catch (IllegalArgumentException e) {
            throw new Exception400("요청 형식이 잘못 되었습니다. 당직 일자를 제대로 입력 하였는지 확인하십시오.");
        }
    }

    @Transactional
    public Schedule updateAnnual(Long id, ScheduleRequestDTO.createAnnualDTO updateDTO) {
        try {
            Schedule schedule = scheduleRepository.findById(id)
                    .orElseThrow(() -> new Exception404("해당 등록 정보가 없습니다."));

            User user = userService.getUser();
            long originalAnnual = ChronoUnit.DAYS.between(schedule.getStartDate(), schedule.getEndDate());

            schedule.setStartDate(updateDTO.getStartDate());
            schedule.setEndDate(updateDTO.getEndDate());
            schedule.setReason(updateDTO.getReason());
            schedule.setEvaluation(EvaluationEnum.STANDBY);

            long updatedAnnual = ChronoUnit.DAYS.between(schedule.getStartDate(), schedule.getEndDate());

            if (originalAnnual != updatedAnnual) {
                user.setAnnual((int) (user.getAnnual() + originalAnnual));
                user.usedAnnual((int) updatedAnnual);
                userRepository.save(user);
            }

            return scheduleRepository.save(schedule);

        } catch (IllegalArgumentException e) {
            throw new Exception400("요청 형식이 잘못 되었습니다. 시작일, 종료일, 사유를 모두 입력 하였는지 확인하십시오.");
        }
    }

    @Transactional
    public Schedule updateDuty(Long id, ScheduleRequestDTO.createDutyDTO updateDTO) {
        try {
            Schedule schedule = scheduleRepository.findById(id)
                    .orElseThrow(() -> new Exception404("해당 등록 정보가 없습니다."));

            if (!schedule.getUser().getId().equals(userService.getUser().getId())) {
                throw new Exception403("접근 권한이 없습니다.");
            }

            schedule.setStartDate(updateDTO.getStartDate());
            schedule.setEndDate(updateDTO.getStartDate());
            schedule.setEvaluation(EvaluationEnum.STANDBY);

            return scheduleRepository.save(schedule);

        } catch (IllegalArgumentException e) {
            throw new Exception400("요청 형식이 잘못 되었습니다. 당직 일자를 제대로 입력 하였는지 확인하십시오.");
        }
    }


    @Transactional
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid schedule id: " + id));


        if (!schedule.getUser().getId().equals(userService.getUser().getId())) {
            throw new Exception403("접근 권한이 없습니다.");
        }

        if (EvaluationEnum.CANCELED.equals(schedule.getEvaluation())) {
            throw new IllegalStateException("이미 취소된 스케줄 입니다.");
        }

        scheduleRepository.updateEvaluationToCanceled(id);
    }

    public List<Schedule> findAllScheduleListByDate(getScheduleReqDTO requestDTO){
        Long hospitalId = userService.getUser().getHospital().getId();

        return scheduleRepository.findByHospitalIdAndEvaluationAndCategoryAndStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual(
                hospitalId, EvaluationEnum.APPROVED, requestDTO.getCategory(), requestDTO.getChooseDate(), requestDTO.getChooseDate());
    }

    public Schedule findByDutyScheduleByDate(getScheduleReqDTO requestDTO){
        Long hospitalId = userService.getUser().getHospital().getId();

        return scheduleRepository.findByHospitalIdAndEvaluationAndCategoryAndStartDate(
                hospitalId, EvaluationEnum.APPROVED, requestDTO.getCategory(), requestDTO.getChooseDate()
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
