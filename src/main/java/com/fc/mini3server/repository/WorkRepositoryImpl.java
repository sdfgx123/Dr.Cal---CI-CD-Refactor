package com.fc.mini3server.repository;

import com.fc.mini3server.domain.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.fc.mini3server.domain.QSchedule.schedule;
import static com.fc.mini3server.domain.QWork.work;

@RequiredArgsConstructor
public class WorkRepositoryImpl implements WorkRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Work> findCalcHospitalUserList(LevelEnum level, String dept, Hospital hospital, LocalDateTime start, LocalDateTime end) {
        return queryFactory.select(work)
                .from(work)
                .where(
                        work.user.hospital.eq(hospital),
                        work.endTime.isNotNull(),
                        eqLevel(level),
                        eqDept(dept),
                        work.startTime.between(start, end)
                ).fetch();
    }

    @Override
    public List<Work> findCalcUserList(LevelEnum level, String dept, Long userId, LocalDateTime start, LocalDateTime end) {
        return queryFactory.select(work)
                .from(work)
                .where(
                        work.user.id.eq(userId),
                        work.endTime.isNotNull(),
                        eqLevel(level),
                        eqDept(dept),
                        work.startTime.between(start, end)
                ).fetch();
    }

    private BooleanExpression eqLevel(LevelEnum level) {
        if (ObjectUtils.isEmpty(level)) {
            return null;
        }
        return work.user.level.eq(level);
    }

    private BooleanExpression eqDept(String dept) {
        if (dept.equals("All")) {
            return null;
        }
        return work.user.dept.name.eq(dept);
    }

    public WorkStatusEnum findUserWorkStatus(Long userId, LocalDateTime start, LocalDateTime end) {

        if (existsStatusOn(userId, start, end))
            return WorkStatusEnum.ON;

        if (existsStatusOFF(userId)) {
            if (existsStatusANNUAL(userId, start, end)) {
                return WorkStatusEnum.ANNUAL;
            }
        }

        return WorkStatusEnum.OFF;
    }

    public Boolean existsStatusOn(Long userId, LocalDateTime start, LocalDateTime end) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(work)
                .where(
                        work.user.id.eq(userId),
                        work.startTime.between(start, end),
                        work.endTime.isNull()
                )
                .fetchFirst();

        return fetchFirst != null;
    }

    private Boolean existsStatusOFF(Long userId) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(work)
                .where(
                        work.user.id.eq(userId),
                        work.startTime.isNotNull().and(work.endTime.isNotNull())
                                .or(work.startTime.isNull().and(work.endTime.isNull()))
                )
                .fetchFirst();

        return fetchFirst != null;
    }

    private Boolean existsStatusANNUAL(Long userId, LocalDateTime start, LocalDateTime end) {
        Integer fetchFirst = queryFactory
                .selectOne()
                .from(schedule)
                .where(
                        schedule.user.id.eq(userId),
                        schedule.category.eq(CategoryEnum.ANNUAL),
                        schedule.evaluation.eq(EvaluationEnum.APPROVED),
                        schedule.startDate.loe(LocalDate.now())
                                .and(schedule.endDate.goe(LocalDate.now()))
                ).fetchFirst();

        return fetchFirst != null;
    }
}
