package com.fc.mini3server.repository;

import com.fc.mini3server.domain.*;
import com.fc.mini3server.dto.AdminResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {

    List<User> findAllByHospitalAndAuthAndLevelIn(Hospital hospital, AuthEnum auth, List<LevelEnum> level);

    Page<AdminResponseDTO.AdminUserListDTO> findByHospitalAndStatusNot(Hospital hospital, StatusEnum status, Pageable pageable);

}
