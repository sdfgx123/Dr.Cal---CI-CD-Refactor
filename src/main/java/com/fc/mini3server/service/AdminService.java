package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server.domain.*;
import com.fc.mini3server.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

import static com.fc.mini3server._core.handler.Message.*;
import static com.fc.mini3server.dto.AdminRequestDTO.*;
import static com.fc.mini3server.dto.AdminResponseDTO.*;
import static com.fc.mini3server.dto.AdminResponseDTO.AdminUserListDTO;
import static com.fc.mini3server.dto.AdminResponseDTO.joinReqListDTO;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final HospitalRepository hospitalRepository;
    private final DeptRepository deptRepository;
    private final WorkRepository workRepository;

    public Page<AdminUserListDTO> findAllUserListAdmin(Pageable pageable) {
        User user = userService.getUser();
        return userRepository.findByHospitalAndStatusNot(user.getHospital(), StatusEnum.NOTAPPROVED, pageable);
    }

    public Page<joinReqListDTO> findAllJoinUserListAdmin(Pageable pageable) {
        User user = userService.getUser();
        return userRepository.findByHospitalAndStatusIs(user.getHospital(), StatusEnum.NOTAPPROVED, pageable);
    }

    @Transactional
    public void updateUserAuth(Long id, editAuthDTO requestDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception400(String.valueOf(id), INVALID_ID_PARAMETER));

        user.updateAuth(requestDTO.getAuth());
    }

    @Transactional
    public void approveUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception400(String.valueOf(id), INVALID_ID_PARAMETER));

        if (!user.getStatus().equals(StatusEnum.NOTAPPROVED)) throw new Exception400(INVALID_USER_STATUS_NOT_APPROVED);

        user.updateStatus(StatusEnum.APPROVED);
    }

    @Transactional
    public void retireUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception400(String.valueOf(id), INVALID_ID_PARAMETER));

        if (!user.getStatus().equals(StatusEnum.APPROVED)) throw new Exception400(INVALID_USER_STATUS_APPROVED);

        user.updateStatus(StatusEnum.RETIRED);
    }

    public List<User> findAllUserListByHospitalIdAdmin(List<LevelEnum> levelList) {
        User user = userService.getUser();

        if (levelList.isEmpty()) levelList.addAll(List.of(LevelEnum.values()));

        return userRepository.findAllByHospitalAndAuthAndLevelIn(user.getHospital(), AuthEnum.USER, levelList);
    }

    @Transactional
    public void createDuty(Long id, createDutyAdminDTO requestDTO) {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER));

        if (user.getDuty() <= 0) {
            throw new Exception400(String.valueOf(id), Message.NO_USER_DUTY_LEFT);
        }

        Hospital hospital = hospitalRepository.findById(user.getHospital().getId()).orElseThrow(() -> new Exception400(String.valueOf(user.getHospital().getId()), Message.HOSPITAL_NOT_FOUND));

        if (scheduleRepository.existsScheduleByHospitalIdAndStartDateAndCategoryAndEvaluation(user.getHospital().getId(), requestDTO.getChooseDate(), CategoryEnum.DUTY, EvaluationEnum.APPROVED))
            throw new Exception400(Message.ALREADY_EXISTS_ON_THAT_DATE);

        Schedule schedule = Schedule.builder().user(user).hospital(hospital).category(CategoryEnum.DUTY).startDate(requestDTO.getChooseDate()).endDate(requestDTO.getChooseDate()).evaluation(EvaluationEnum.APPROVED).build();

        scheduleRepository.save(schedule);

        user.approvedDuty();
    }

    public Page<Schedule> findAnnualList(Pageable pageable) {
        User user = userService.getUser();
        return scheduleRepository.findByHospitalAndCategoryIsOrderById(user.getHospital(), CategoryEnum.ANNUAL, pageable);
    }

    public Page<Schedule> findDutyList(Pageable pageable) {
        User user = userService.getUser();
        return scheduleRepository.findByHospitalAndCategoryIsOrderById(user.getHospital(), CategoryEnum.DUTY, pageable);
    }

    @Transactional
    public void updateScheduleEvaluation(Long id, editEvaluationDTO requestDTO) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER));

        if (schedule.getEvaluation().equals(EvaluationEnum.APPROVED) || schedule.getEvaluation().equals(EvaluationEnum.REJECTED) || schedule.getEvaluation().equals(EvaluationEnum.CANCELED))
            throw new Exception400(Message.INVALID_SCHEDULE_EVALUATION);


        if (schedule.getCategory().equals(CategoryEnum.DUTY) && requestDTO.getEvaluation().equals(EvaluationEnum.APPROVED)) {
            Schedule originSchedule = scheduleRepository.findByHospitalIdAndEvaluationAndCategoryAndStartDate(schedule.getHospital().getId(), EvaluationEnum.APPROVED, CategoryEnum.DUTY, schedule.getStartDate()).orElseThrow(() -> new Exception400(NO_DUTY_SCHEDULE_ON_DATE));
            Schedule changeSchedule = scheduleRepository.findByHospitalIdAndEvaluationAndCategoryAndStartDate(schedule.getHospital().getId(), EvaluationEnum.APPROVED, CategoryEnum.DUTY, schedule.getEndDate()).orElseThrow(() -> new Exception400(NO_DUTY_SCHEDULE_ON_DATE));

            Schedule newSchedule = Schedule.builder().user(changeSchedule.getUser()).hospital(changeSchedule.getHospital()).category(CategoryEnum.DUTY).startDate(schedule.getStartDate()).endDate(schedule.getStartDate()).evaluation(EvaluationEnum.APPROVED).build();

            Schedule newSchedule2 = Schedule.builder().user(schedule.getUser()).hospital(schedule.getHospital()).category(CategoryEnum.DUTY).startDate(schedule.getEndDate()).endDate(schedule.getEndDate()).evaluation(EvaluationEnum.APPROVED).build();

            scheduleRepository.saveAll(Arrays.asList(newSchedule, newSchedule2));

            schedule.updateEvaluation(EvaluationEnum.COMPLETED);
            originSchedule.updateEvaluation(EvaluationEnum.CANCELED);
            changeSchedule.updateEvaluation(EvaluationEnum.CANCELED);
        }

        if (schedule.getCategory().equals(CategoryEnum.DUTY) && requestDTO.getEvaluation().equals(EvaluationEnum.REJECTED)) {
            schedule.updateEvaluation(requestDTO.getEvaluation());
        }

        if (schedule.getCategory().equals(CategoryEnum.ANNUAL) && requestDTO.getEvaluation().equals(EvaluationEnum.APPROVED))
            schedule.updateEvaluation(requestDTO.getEvaluation());

        if (schedule.getCategory().equals(CategoryEnum.ANNUAL) && requestDTO.getEvaluation().equals(EvaluationEnum.REJECTED)) {
            User user = schedule.getUser();

            long annual = ChronoUnit.DAYS.between(schedule.getStartDate(), schedule.getEndDate());
            user.recoverAnnual((int) annual);

            schedule.updateEvaluation(requestDTO.getEvaluation());
        }
    }

    @Transactional
    public void deleteDuty(Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new Exception400(String.valueOf(id), INVALID_ID_PARAMETER));

        if (!schedule.getCategory().equals(CategoryEnum.DUTY))
            throw new Exception400(Message.INVALID_SCHEDULE_CATEGORY_NOT_DUTY);

        scheduleRepository.delete(schedule);
    }

    public UserWorkDashBoardDTO findUserWorkDashBoard(LevelEnum level, String dept) {
        User user = userService.getUser();

        if (!deptRepository.existsByNameAndHospital(dept, user.getHospital()))
            dept = "All";

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate startOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate startOfLastMonth = today.minusMonths(1).withDayOfMonth(1);
        LocalDate endOfLastMonth = today.minusMonths(1).withDayOfMonth(today.lengthOfMonth());

        String dayWork = parseDuration(calculateWorkTime(user, level, dept, today, today));
        String yesterdayWorkTime = parseDuration(calculateWorkTime(user, level, dept, yesterday, yesterday));
        String weekWork = parseDuration(calculateWorkTime(user, level, dept, startOfWeek, endOfWeek));
        String lastWeekWorkTime = parseDuration(calculateWorkTime(user, level, dept, startOfLastWeek, endOfLastWeek));
        String monthWork = parseDuration(calculateWorkTime(user, level, dept, startOfMonth, endOfMonth));
        String lastMonthWorkTime = parseDuration(calculateWorkTime(user, level, dept, startOfLastMonth, endOfLastMonth));

        return UserWorkDashBoardDTO.builder()
                .dayWork(dayWork)
                .yesterdayWorkTime(yesterdayWorkTime)
                .weekWork(weekWork)
                .lastWeekWorkTime(lastWeekWorkTime)
                .monthWork(monthWork)
                .lastMonthWorkTime(lastMonthWorkTime)
                .build();
    }

    public Page<UserWorkListDTO> findUserWorkList(LevelEnum level, String dept, Pageable pageable) {

        //        List<UserWorkListDTO> workList = workRepository.findUserWorkListByHospital(level, dept, user.getHospital());


        //        return workRepository.findUserWorkListByHospital(level, dept, user.getHospital(), pageable);


        return null;
    }

    public String parseDuration(Duration duration) {
        Long totalSeconds = duration.getSeconds();
        Long hours = totalSeconds / 3600;
        Long minutes = (totalSeconds % 3600) / 60;
        Long seconds = totalSeconds % 60;

        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }
    public Duration calculateWorkTime(User user, LevelEnum level, String dept, LocalDate start, LocalDate end) {

        List<Work> works = workRepository.findCalcUserList(level, dept, user.getHospital(), start.atStartOfDay(), end.atTime(23, 59, 59));

        Duration totalDuration = Duration.ZERO;

        for (Work work : works) {
            LocalDateTime endTime = work.getEndTime() != null ? work.getEndTime() : LocalDateTime.now();
            Duration duration = Duration.between(work.getStartTime(), endTime);
            totalDuration = totalDuration.plus(duration);
        }

        return totalDuration;
    }


}
