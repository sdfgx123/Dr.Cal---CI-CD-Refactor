package com.fc.mini3server.repository;

import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.Work;

import java.time.LocalDateTime;
import java.util.List;

import static com.fc.mini3server.dto.AdminResponseDTO.*;

public interface WorkRepositoryCustom {

    List<UserWorkListDTO> findUserWorkListByHospital(LevelEnum level, String dept, Hospital hospital);

    List<Work> findCalcUserList(LevelEnum level, String dept, Hospital hospital, LocalDateTime start, LocalDateTime end);
}
