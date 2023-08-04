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

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Page<Schedule> findByCategoryIsOrderById(CategoryEnum category, Pageable pageable);
    Page<Schedule> findByEvaluation(EvaluationEnum evaluation, Pageable pageable);

    @Modifying
    @Query("UPDATE schedule_tb s SET s.evaluation = 'CANCELED' WHERE s.id = :scheduleId")
    void updateEvaluationToCanceled(@Param("scheduleId") Long scheduleId);
}
