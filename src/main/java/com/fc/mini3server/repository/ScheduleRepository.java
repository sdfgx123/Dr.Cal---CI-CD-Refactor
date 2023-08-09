package com.fc.mini3server.repository;

import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.EvaluationEnum;
import com.fc.mini3server.domain.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM schedule_tb s where s.category = :category ORDER BY CASE WHEN s.evaluation = 'STANDBY' THEN 1 ELSE 2 END")
    Page<Schedule> findByCategoryIsOrderById(@Param("category") CategoryEnum category, Pageable pageable);
    List<Schedule> findByEvaluationAndUserHospitalId(EvaluationEnum evaluation, Long hospitalId);
    List<Schedule> findByHospitalIdAndEvaluationAndCategoryAndStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual(Long hospitalId, EvaluationEnum evaluation, CategoryEnum category, LocalDate startDate, LocalDate endDate);
    Optional<Schedule> findByHospitalIdAndEvaluationAndCategoryAndStartDate(Long HospitalId, EvaluationEnum evaluation, CategoryEnum category, LocalDate startDate);
    Optional<Schedule> findByHospitalIdAndEvaluationAndCategoryAndStartDateAndEndDate(Long hospitalId, EvaluationEnum evaluationEnum, CategoryEnum category, LocalDate chooseDate, LocalDate chooseDate1);
    List<Schedule> findByUserId(Long userId);
    List<Schedule> findByEvaluationIn(List<EvaluationEnum> evaluations);

    @Modifying
    @Query("UPDATE schedule_tb s SET s.evaluation = 'CANCELED' WHERE s.id = :scheduleId")
    void updateEvaluationToCanceled(@Param("scheduleId") Long scheduleId);

    boolean existsScheduleByHospitalIdAndStartDateAndCategoryAndEvaluation(Long id, LocalDate chooseDate, CategoryEnum categoryEnum, EvaluationEnum evaluationEnum);

    boolean existsByHospitalIdAndEvaluationAndCategoryAndEndDate(Long id, EvaluationEnum evaluationEnum, CategoryEnum categoryEnum, LocalDate updateDate);

    boolean existsByUserIdAndCategoryAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndEvaluationIn(Long userId, CategoryEnum category, LocalDate startDate, LocalDate endDate, List<EvaluationEnum> evaluations);

}