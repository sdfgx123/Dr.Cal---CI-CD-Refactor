package com.fc.mini3server.repository;

import com.fc.mini3server.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.fc.mini3server.dto.AdminResponseDTO.AdminUserListDTO;
import static com.fc.mini3server.dto.AdminResponseDTO.joinReqListDTO;

public interface UserRepositoryCustom {

    List<User> findAllByHospitalAndAuthAndLevelIn(Hospital hospital, AuthEnum auth, List<LevelEnum> level);

    Page<AdminUserListDTO> findByHospitalAndStatusNot(Hospital hospital, StatusEnum status, Pageable pageable);

    Page<joinReqListDTO> findByHospitalAndStatusIs(Hospital hospital, StatusEnum status, Pageable pageable);
}
