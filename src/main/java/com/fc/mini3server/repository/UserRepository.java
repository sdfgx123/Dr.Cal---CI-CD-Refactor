package com.fc.mini3server.repository;

import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.StatusEnum;
import com.fc.mini3server.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findTopByOrderByEmpNoDesc();

    @Query("SELECT u FROM user_tb u where u.status <> :status ORDER BY CASE WHEN u.status = 'APPROVED' THEN 1 WHEN u.status = 'RETIRED' THEN 2 ELSE 1 END")
    Page<User> findByStatusNot(@Param("status") StatusEnum status, Pageable pageable);

    Page<User> findByStatusIs(StatusEnum status, Pageable pageable);

    List<User> findAllByHospitalIdAndLevelIn(Long hospitalId, List<LevelEnum> level);
}
