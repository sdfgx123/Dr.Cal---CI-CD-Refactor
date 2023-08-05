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

    Page<Schedule> findByCategoryIsOrderById(CategoryEnum category, Pageable pageable);
    Page<Schedule> findByEvaluation(EvaluationEnum evaluation, Pageable pageable);
    List<Schedule> findByEvaluationAndCategoryAndStartDateIsLessThanEqualAndEndDateIsGreaterThanEqual(EvaluationEnum evaluation, CategoryEnum category, LocalDate startDate, LocalDate endDate);
    Optional<Schedule> findByEvaluationAndCategoryAndStartDate(EvaluationEnum evaluation, CategoryEnum category, LocalDate startDate);
    List<Schedule> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE schedule_tb s SET s.evaluation = 'CANCELED' WHERE s.id = :scheduleId")
    void updateEvaluationToCanceled(@Param("scheduleId") Long scheduleId);
}
