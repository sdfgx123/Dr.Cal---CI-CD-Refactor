package com.fc.mini3server.repository;

import com.fc.mini3server.domain.AuthEnum;
import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.fc.mini3server.domain.QUser.user;

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
}
