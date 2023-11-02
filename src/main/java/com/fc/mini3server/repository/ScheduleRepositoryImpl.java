package com.fc.mini3server.repository;

import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.EvaluationEnum;
import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.Schedule;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.fc.mini3server.domain.QSchedule.schedule;

@RequiredArgsConstructor
public class ScheduleRepositoryImpl implements ScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Schedule> findByHospitalAndCategoryIsOrderById(Hospital hospital, CategoryEnum category, Pageable pageable) {

        NumberExpression<Integer> evaluationSort = new CaseBuilder()
                .when(schedule.evaluation.eq(EvaluationEnum.STANDBY)).then(1)
                .otherwise(2);

        List<Schedule> content = queryFactory
                .selectFrom(schedule)
                .where(
                        schedule.hospital.eq(hospital),
                        schedule.category.eq(category)
                )
                .orderBy(
                        evaluationSort.asc(),
                        schedule.updatedAt.asc(),
                        schedule.id.asc()
                )
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(schedule.count())
                .from(schedule)
                .where(schedule.hospital.eq(hospital),
                        schedule.category.eq(category)
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }
}
