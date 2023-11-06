package com.fc.mini3server.repository;

import com.fc.mini3server.domain.*;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static com.fc.mini3server.domain.QUser.user;
import static com.fc.mini3server.dto.AdminResponseDTO.AdminUserListDTO;
import static com.fc.mini3server.dto.AdminResponseDTO.joinReqListDTO;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findAllByHospitalAndAuthAndLevelIn(Hospital hospital, AuthEnum auth, List<LevelEnum> level) {
        return queryFactory.selectFrom(user)
                .where(
                        user.hospital.eq(hospital),
                        user.auth.eq(auth),
                        user.level.in(level)
                )
                .fetch();
    }

    @Override
    public Page<AdminUserListDTO> findByHospitalAndStatusNot(Hospital hospital, StatusEnum status, Pageable pageable) {

        NumberExpression<Integer> statusSort = new CaseBuilder()
                .when(user.status.eq(StatusEnum.APPROVED)).then(1)
                .when(user.status.eq(StatusEnum.RETIRED)).then(2)
                .otherwise(1);

        List<AdminUserListDTO> content = queryFactory
                .select(
                        Projections.constructor(AdminUserListDTO.class,
                                user.id,
                                user.name,
                                user.phone,
                                user.hospital.name,
                                user.dept.name,
                                user.level,
                                user.auth,
                                user.status
                        )
                )
                .from(user)
                .where(
                        user.hospital.eq(hospital),
                        user.status.ne(status)
                )
                .orderBy(
                        user.createdAt.desc(),
                        statusSort.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.hospital.eq(hospital),
                        user.status.ne(status)
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    @Override
    public Page<joinReqListDTO> findByHospitalAndStatusIs(Hospital hospital, StatusEnum status, Pageable pageable) {
        List<joinReqListDTO> content = queryFactory
                .select(Projections.constructor(joinReqListDTO.class,
                        user.id,
                        user.name,
                        user.phone,
                        user.hospital.name,
                        user.dept.name,
                        user.level,
                        user.status))
                .from(user)
                .where(
                        user.hospital.eq(hospital),
                        user.status.eq(status)
                )
                .orderBy(user.createdAt.desc())
                .fetch();

        JPAQuery<Long> count =
                queryFactory.select(user.count())
                        .from(user)
                        .where(
                                user.hospital.eq(hospital),
                                user.status.eq(status)
                        );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    @Override
    public Page<User> findAllByAuthAndLevelAndHospitalAndDept(AuthEnum auth, LevelEnum level, Hospital hospital, String dept, Pageable pageable) {
        List<User> content = queryFactory.selectFrom(user)
                .where(
                        user.auth.eq(auth),
                        eqLevel(level),
                        user.hospital.eq(hospital),
                        eqDept(dept)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        user.auth.eq(auth),
                        eqLevel(level),
                        user.hospital.eq(hospital),
                        eqDept(dept)
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression eqLevel(LevelEnum level) {
        if (ObjectUtils.isEmpty(level)) {
            return null;
        }
        return user.level.eq(level);
    }

    private BooleanExpression eqDept(String dept) {
        if (dept.equals("All")) {
            return null;
        }
        return user.dept.name.eq(dept);
    }
}
