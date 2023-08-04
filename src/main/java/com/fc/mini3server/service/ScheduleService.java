package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server.domain.*;
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

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final HospitalRepository hospitalRepository;

    @Autowired
    public ScheduleService(UserService userService, ScheduleRepository scheduleRepository, HospitalRepository hospitalRepository, UserRepository userRepository) {
        this.userService = userService;
        this.scheduleRepository = scheduleRepository;
        this.hospitalRepository = hospitalRepository;
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

    public Page<ScheduleResponseDTO.ApprovedScheduleListDTO> getApprovedSchedule(Pageable pageable) {
        return scheduleRepository.findByEvaluation(EvaluationEnum.APPROVED, pageable)
                .map(ScheduleResponseDTO.ApprovedScheduleListDTO::of);
    }

    public Schedule createAnnualSchedule(ScheduleRequestDTO.createAnnualDTO createAnnualDTO) {
        try {
            /*
            User user = userService.getUser();
            Long userId = user.getId();
             */

             User user = userRepository.findById(createAnnualDTO.getUser().getId())
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

            User user = userRepository.findById(createDutyDTO.getUser().getId())
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
}
