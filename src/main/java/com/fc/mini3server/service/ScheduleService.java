package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server._core.handler.exception.Exception403;
import com.fc.mini3server.domain.*;
import com.fc.mini3server._core.handler.exception.Exception404;
import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.EvaluationEnum;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.dto.AdminRequestDTO;
import com.fc.mini3server.dto.ScheduleRequestDTO;
import com.fc.mini3server.dto.ScheduleResponseDTO;
import com.fc.mini3server.repository.HospitalRepository;
import com.fc.mini3server.repository.ScheduleRepository;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fc.mini3server.dto.ScheduleRequestDTO.*;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public ScheduleService(UserService userService, ScheduleRepository scheduleRepository, UserRepository userRepository) {
        this.userService = userService;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }


    public Page<Schedule> findAnnualList(Pageable pageable) {
        return scheduleRepository.findByCategoryIsOrderById(CategoryEnum.ANNUAL, pageable);
    }

    public Page<Schedule> findDutyList(Pageable pageable) {
        return scheduleRepository.findByCategoryIsOrderById(CategoryEnum.DUTY, pageable);
    }

    @Transactional
    public void updateScheduleEvaluation(Long id, AdminRequestDTO.editEvaluationDTO requestDTO) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER));

        schedule.updateEvaluation(requestDTO.getEvaluation());
    }

    public List<ScheduleResponseDTO.ApprovedScheduleListDTO> getApprovedSchedule() {
        Long hospitalId = userService.getUser().getHospital().getId();
        return ScheduleResponseDTO.ApprovedScheduleListDTO.listOf(scheduleRepository.findByEvaluationAndUserHospitalId(EvaluationEnum.APPROVED, hospitalId));
    }


    public Schedule createAnnualSchedule(ScheduleRequestDTO.createAnnualDTO createAnnualDTO) {
        try {

            User user = userRepository.findById(userService.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("invalid user id : " + createAnnualDTO.getUser().getId()));

            Schedule schedule = Schedule.builder()
                    .user(user)
                    .hospital(user.getHospital())
                    .category(CategoryEnum.ANNUAL)
                    .startDate(createAnnualDTO.getStartDate())
                    .endDate(createAnnualDTO.getEndDate())
                    .evaluation(EvaluationEnum.STANDBY)
                    .reason(createAnnualDTO.getReason())
                    .build();

            return scheduleRepository.save(schedule);

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

            if (!schedule.getUser().getId().equals(userService.getUser().getId())) {
                throw new Exception403("접근 권한이 없습니다.");
            }

            schedule.setStartDate(updateDTO.getStartDate());
            schedule.setEndDate(updateDTO.getEndDate());
            schedule.setReason(updateDTO.getReason());
            schedule.setEvaluation(EvaluationEnum.STANDBY);

            Schedule updatedSchedule = scheduleRepository.save(schedule);
            return updatedSchedule;

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

            Schedule updatedSchedule = scheduleRepository.save(schedule);
            return updatedSchedule;

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
