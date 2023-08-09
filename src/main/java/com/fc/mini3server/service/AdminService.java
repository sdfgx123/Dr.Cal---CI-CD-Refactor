package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server.domain.*;
import com.fc.mini3server.dto.AdminRequestDTO;
import com.fc.mini3server.repository.HospitalRepository;
import com.fc.mini3server.repository.ScheduleRepository;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fc.mini3server._core.handler.Message.*;
import static com.fc.mini3server._core.handler.Message.HOSPITAL_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final HospitalRepository hospitalRepository;

    public Page<User> findAllUserListAdmin(Pageable pageable){
        return userRepository.findByStatusNot(StatusEnum.NOTAPPROVED, pageable);
    }

    public Page<User> findAllJoinUserListAdmin(Pageable pageable) {
        return userRepository.findByStatusIs(StatusEnum.NOTAPPROVED, pageable);
    }

    @Transactional
    public void updateUserAuth(Long id, AdminRequestDTO.editAuthDTO requestDTO) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), INVALID_ID_PARAMETER));

        user.updateAuth(requestDTO.getAuth());
    }

    @Transactional
    public void approveUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), INVALID_ID_PARAMETER));

        if (!user.getStatus().equals(StatusEnum.NOTAPPROVED))
            throw new Exception400(INVALID_USER_STATUS_NOT_APPROVED);

        user.updateStatus(StatusEnum.APPROVED);
    }

    @Transactional
    public void retireUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), INVALID_ID_PARAMETER));

        if (!user.getStatus().equals(StatusEnum.APPROVED))
            throw new Exception400(INVALID_USER_STATUS_APPROVED);

        user.updateStatus(StatusEnum.RETIRED);
    }

    public List<User> findAllUserListByHospitalIdAdmin(Long hospitalId, List<LevelEnum> levelList) {
        hospitalRepository.findById(hospitalId).orElseThrow(
                () -> new Exception400(String.valueOf(hospitalId), HOSPITAL_NOT_FOUND)
        );

        if(levelList.isEmpty())
            levelList.addAll(List.of(LevelEnum.values()));

        return userRepository.findAllByHospitalIdAndLevelIn(hospitalId, levelList);
    }

    @Transactional
    public void createDuty(Long id, AdminRequestDTO.createDutyAdminDTO requestDTO) {

        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER)
        );

        if (user.getDuty() <= 0){
            throw new Exception400(String.valueOf(id), Message.NO_USER_DUTY_LEFT);
        }

        Hospital hospital = hospitalRepository.findById(user.getHospital().getId()).orElseThrow(
                () -> new Exception400(String.valueOf(user.getHospital().getId()), Message.HOSPITAL_NOT_FOUND)
        );

        if(scheduleRepository.existsScheduleByStartDateAndCategoryAndEvaluation(
                requestDTO.getChooseDate(), CategoryEnum.DUTY, EvaluationEnum.APPROVED))
            throw new Exception400(Message.ALREADY_EXISTS_ON_THAT_DATE);

        Schedule schedule = Schedule.builder()
                .user(user)
                .hospital(hospital)
                .category(CategoryEnum.DUTY)
                .startDate(requestDTO.getChooseDate())
                .endDate(requestDTO.getChooseDate())
                .evaluation(EvaluationEnum.APPROVED)
                .build();

        scheduleRepository.save(schedule);

        user.approvedDuty();
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

    @Transactional
    public void deleteDuty(Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), INVALID_ID_PARAMETER)
        );

        if (!schedule.getCategory().equals(CategoryEnum.DUTY))
            throw new Exception400(Message.INVALID_SCHEDULE_CATEGORY_NOT_DUTY);

        scheduleRepository.delete(schedule);
    }
}
