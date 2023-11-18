package com.fc.mini3server.repository;

import com.fc.mini3server.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;


public interface WorkRepositoryCustom {
    List<Work> findCalcHospitalUserList(LevelEnum level, String dept, Hospital hospital, LocalDateTime start, LocalDateTime end);
    List<Work> findCalcUserList(LevelEnum level, String dept, Long userId, LocalDateTime start, LocalDateTime end);
    WorkStatusEnum findUserWorkStatus(Long userId, LocalDateTime start, LocalDateTime end);
    Work findFirstWorkByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    Page<Work> findWorkByUserOrderByStartTimeDesc(User user, Pageable pageable);
}
