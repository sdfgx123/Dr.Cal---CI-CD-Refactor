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
    Page<Schedule> findByEvaluationAndUserHospitalId(EvaluationEnum evaluation, Long hospitalId, Pageable pageable);
    List<Schedule> findByHospitalIdAndEvaluationAndCategoryAndStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual(Long hospitalId, EvaluationEnum evaluation, CategoryEnum category, LocalDate startDate, LocalDate endDate);
    Optional<Schedule> findByHospitalIdAndEvaluationAndCategoryAndStartDate(Long HospitalId, EvaluationEnum evaluation, CategoryEnum category, LocalDate startDate);
    List<Schedule> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE schedule_tb s SET s.evaluation = 'CANCELED' WHERE s.id = :scheduleId")
    void updateEvaluationToCanceled(@Param("scheduleId") Long scheduleId);
}
