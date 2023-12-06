package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server.domain.*;
import com.fc.mini3server.dto.AdminRequestDTO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.fc.mini3server._core.handler.Message.*;
import static com.fc.mini3server.dto.AdminRequestDTO.*;
import static com.fc.mini3server.dto.AdminResponseDTO.*;
import static java.lang.Math.abs;

@RequiredArgsConstructor
@Service
public class AdminService {
    private static final LocalDate today = LocalDate.now();
    private static final LocalDate yesterday = today.minusDays(1);
    private static final LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    private static final LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    private static final LocalDate startOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    private static final LocalDate endOfLastWeek = today.minusWeeks(1).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    private static final LocalDate startOfMonth = today.withDayOfMonth(1);
    private static final LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
    private static final LocalDate startOfLastMonth = today.minusMonths(1).withDayOfMonth(1);
    private static final LocalDate endOfLastMonth = today.withDayOfMonth(1).minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth());
    private final UserService userService;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final HospitalRepository hospitalRepository;
    private final DeptRepository deptRepository;
    private final WorkRepository workRepository;

    private static Duration getTotalDuration(List<Work> works) {
        Duration totalDuration = Duration.ZERO;

        for (Work work : works) {
            LocalDateTime endTime = work.getEndTime() != null ? work.getEndTime() : LocalDateTime.now();
            Duration duration = Duration.between(work.getStartTime(), endTime);
            totalDuration = totalDuration.plus(duration);
        }
        return totalDuration;
    }

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

        Duration dayWork = calculateWorkTime(user, level, dept, today, today);
        Duration yesterdayWorkTime = dayWork.minus(calculateWorkTime(user, level, dept, yesterday, yesterday));
        Duration weekWork = calculateWorkTime(user, level, dept, startOfWeek, endOfWeek);
        Duration lastWeekWorkTime = weekWork.minus(calculateWorkTime(user, level, dept, startOfLastWeek, endOfLastWeek));
        Duration monthWork = calculateWorkTime(user, level, dept, startOfMonth, endOfMonth);
        Duration lastMonthWorkTime = monthWork.minus(calculateWorkTime(user, level, dept, startOfLastMonth, endOfLastMonth));

        return UserWorkDashBoardDTO.builder()
                .dayWork(parseDuration(dayWork))
                .yesterdayWorkTime(parseDuration(yesterdayWorkTime))
                .weekWork(parseDuration(weekWork))
                .lastWeekWorkTime(parseDuration(lastWeekWorkTime))
                .monthWork(parseDuration(monthWork))
                .lastMonthWorkTime(parseDuration(lastMonthWorkTime))
                .build();
    }

    public UserWorkListPageDTO findUserWorkList(LevelEnum level, String dept, Pageable pageable) {
        User user = userService.getUser();

        if (!deptRepository.existsByNameAndHospital(dept, user.getHospital()))
            dept = "All";

        Page<User> userPageList = userRepository.findAllByAuthAndLevelAndHospitalAndDept(
                AuthEnum.USER, level, user.getHospital(), dept, pageable);

        List<User> userList = userPageList.getContent();

        List<findUserWorkTimeDTO> UserWorkList = new ArrayList<>();

        for (User u : userList) {

            WorkStatusEnum status = workRepository.findUserWorkStatus(u.getId(), today.atStartOfDay(), today.atTime(23, 59, 59));

            UserWorkList.add(AdminRequestDTO.findUserWorkTimeDTO.builder()
                    .userId(u.getId())
                    .username(u.getName())
                    .deptId(u.getDept().getId())
                    .level(u.getLevel())
                    .todayWorkTime(parseDuration(calculateUserWorkTime(u, level, dept, today, today)))
                    .weekWorkTime(parseDuration(calculateUserWorkTime(u, level, dept, startOfWeek, endOfWeek)))
                    .monthWorkTime(parseDuration(calculateUserWorkTime(u, level, dept, startOfMonth, endOfMonth)))
                    .status(status)
                    .build());
        }

        return new UserWorkListPageDTO(userPageList.getTotalPages(), UserWorkListDTO.listOf(UserWorkList));
    }

    public String parseDuration(Duration duration) {
        long totalSeconds = duration.getSeconds();
        Long hours = totalSeconds / 3600;
        Long minutes = abs((totalSeconds % 3600) / 60);

        return String.format("%d시간 %02d분", hours, minutes);
    }

    public Duration calculateWorkTime(User user, LevelEnum level, String dept, LocalDate start, LocalDate end) {

        List<Work> works = workRepository.findCalcHospitalUserList(level, dept, user.getHospital(), start.atStartOfDay(), end.atTime(23, 59, 59));

        return getTotalDuration(works);
    }

    public Duration calculateUserWorkTime(User user, LevelEnum level, String dept, LocalDate start, LocalDate end) {

        List<Work> works = workRepository.findCalcUserList(level, dept, user.getId(), start.atStartOfDay(), end.atTime(23, 59, 59));

        return getTotalDuration(works);
    }
}
