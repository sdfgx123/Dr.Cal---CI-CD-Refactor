package com.fc.mini3server.repository;

import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.LevelEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static com.fc.mini3server.dto.AdminResponseDTO.UserWorkListDTO;

public interface WorkRepositoryCustom {

    Page<UserWorkListDTO> findUserWorkListByHospital(LevelEnum level, String dept, Hospital hospital, Pageable pageable);
}
