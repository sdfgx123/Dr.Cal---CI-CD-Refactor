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
import com.fc.mini3server.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.fc.mini3server.dto.ScheduleRequestDTO.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;
    private final UserService userService;

    private final StringRedisTemplate redisTemplate;

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
    public void deleteAnnual(Long id) {
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

    public void startWork(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception400("유저를 찾지 못했습니다. 올바른 유저가 요청했는지 확인하십시오."));

        Optional<Work> latestWork = workRepository.findTopByUserIdOrderByStartTimeDesc(userId);
        if (latestWork.isPresent()) {
            Work lastWork = latestWork.get();
            if (lastWork.getStartTime().toLocalDate().isEqual(LocalDate.now())) {
                throw new Exception400("이미 출근했습니다.");
            }
        }

        Work work = new Work();
        work.setUser(user);
        work.setStartTime(LocalDateTime.now());
        workRepository.save(work);
    }

    public void startWorkUsingRedis(Long userId) {
        log.info("startWorkUsingRedis START");
        String lockKey = "lock:" + userId;
        String lockVal = UUID.randomUUID().toString();

        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockVal, 10, TimeUnit.SECONDS);
        log.info("lockKey : " + lockKey + ", " + "lockVal : " + lockVal);
        log.info("acquired : " + acquired);

        if (! Boolean.TRUE.equals(acquired)) {
            log.info("락 획득 실패, 다른 스레드가 우선순위를 가짐");
            return;
        }

        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new Exception400("유저를 찾지 못했습니다. 올바른 유저가 요청했는지 확인하십시오."));

            Optional<Work> latestWork = workRepository.findTopByUserIdOrderByStartTimeDesc(userId);
            if (latestWork.isPresent()) {
                Work lastWork = latestWork.get();
                if (lastWork.getStartTime().toLocalDate().isEqual(LocalDate.now())) {
                    throw new Exception400("이미 출근했습니다.");
                }
            }

            Work work = new Work();
            work.setUser(user);
            work.setStartTime(LocalDateTime.now());
            workRepository.save(work);
            log.info("INSERT to DB safely");

        } finally {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Collections.singletonList(lockKey), lockVal);
        }
    }

    public void endWork(Long userId) {
        Work work = workRepository.findTopByUserIdOrderByStartTimeDesc(userId)
                        .orElseThrow(() -> new RuntimeException("유저를 찾지 못했습니다."));
        work.setEndTime(LocalDateTime.now());
        workRepository.save(work);
    }
}
