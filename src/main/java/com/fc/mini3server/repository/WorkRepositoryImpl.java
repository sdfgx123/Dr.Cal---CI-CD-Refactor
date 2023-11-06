package com.fc.mini3server.repository;

import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.Work;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.fc.mini3server.domain.QWork.work;
import static com.fc.mini3server.dto.AdminResponseDTO.UserWorkListDTO;

@RequiredArgsConstructor
public class WorkRepositoryImpl implements WorkRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Work> findCalcUserList(LevelEnum level, String dept, Hospital hospital, LocalDateTime start, LocalDateTime end) {
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
    public List<UserWorkListDTO> findUserWorkListByHospital(LevelEnum level, String dept, Hospital hospital) {


        List<UserWorkListDTO> content = queryFactory
                .select(
                        Projections.constructor(UserWorkListDTO.class,
                                work.user.id,
                                work.user.name,
                                work.user.dept.id,
                                work.user.level,
                                work.startTime,
                                work.endTime
//                                JPAExpressions
//                                        .select(
//                                                schedule.count().as("status"))
//                                        .from(schedule)
//                                        .where(
//                                                schedule.category.eq(CategoryEnum.ANNUAL),
//                                                schedule.evaluation.eq(EvaluationEnum.APPROVED),
//                                                schedule.user.id.eq(work.user.id),
//                                                schedule.startDate.eq(LocalDate.now())
//                                        )
                        )
                )
                .from(work)
                .where(
                        work.user.hospital.eq(hospital),
                        work.endTime.isNotNull(),
                        eqLevel(level),
                        eqDept(dept)
                )
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
                .fetch();

        return content;

//
//        JPAQuery<Long> count = queryFactory
//                .select(work.count())
//                .from(work)
//                .where(
//                        work.user.hospital.eq(hospital),
//                        eqLevel(level),
//                        eqDept(dept)
//                );

//        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
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

//    private WorkStatusEnum findWorkStatus() {
//        return queryFactory.select(
//                new CaseBuilder()
//                        .when(
//                                work.startTime.isNotNull(),
//                                work.endTime.isNull()
//                        ).then()
//
//
//        ).from(work)
//                .leftJoin(schedule).on(
//                        work.user.eq(schedule.user),
//                        schedule.category.eq(CategoryEnum.ANNUAL),
//                        schedule.startDate.eq(LocalDate.now())
//                )
//                .fetchFirst();
//
//    }

}
