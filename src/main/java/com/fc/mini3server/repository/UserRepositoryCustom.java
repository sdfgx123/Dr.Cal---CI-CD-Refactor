package com.fc.mini3server.repository;

import com.fc.mini3server.domain.AuthEnum;
import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.User;

import java.util.List;

public interface UserRepositoryCustom {

    List<User> findAllByHospitalAndAuthAndLevelIn(Hospital hospital, AuthEnum auth, List<LevelEnum> level);

}
