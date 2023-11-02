package com.fc.mini3server.repository;

import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.WorkStatusEnum;
import com.fc.mini3server.dto.AdminResponseDTO.UserWorkListDTO;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.fc.mini3server.domain.QSchedule.schedule;
import static com.fc.mini3server.domain.QUser.user;
import static com.fc.mini3server.domain.QWork.work;

@RequiredArgsConstructor
public class WorkRepositoryImpl implements WorkRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UserWorkListDTO> findUserWorkListByHospital(LevelEnum level, String dept, Hospital hospital, Pageable pageable) {

//        Expression<String> dateDifference = Expressions.stringTemplate(
//                "TIMESTAMPDIFF( MINUTE, {0}, {1} )",
//                work.endTime, work.startTime);

        List<UserWorkListDTO> content = queryFactory
                .select(
                        Projections.constructor(UserWorkListDTO.class,
                                work.user.id,
                                work.user.name,
                                work.user.dept.id,
                                work.user.level,
                                ExpressionUtils.as(
                                        Expressions.asString(
                                                ""
                                        ), "todayWorkTime"
                                ),
                                ExpressionUtils.as(
                                        Expressions.asString(
                                                ""
                                        ), "weekWorkTime"
                                ),
                                ExpressionUtils.as(
                                        Expressions.asString(
                                                ""
                                        ), "monthWorkTime"
                                ),
                                ExpressionUtils.as(
                                        Expressions.asEnum(
//                                                findWorkStatus()
                                                WorkStatusEnum.OFF
                                        ), "status"
                                )
                        )
                )
                .from(work)
//                .innerJoin()
                .where(
                        eqLevel(level),
                        work.user.hospital.eq(hospital),
                        work.user.dept.name.eq(dept)
                )
                .groupBy(work.user.name)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(work.count())
                .from(work)
                .where(
                        eqLevel(level),
                        work.user.hospital.eq(hospital),
                        work.user.dept.name.eq(dept)
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression eqLevel(LevelEnum level) {
        if (ObjectUtils.isEmpty(level)) {
            return null;
        }
        return work.user.level.eq(level);
    }

//    private WorkStatusEnum findWorkStatus() {
//
//        return queryFactory.select(
//
//        )
//    }

}
