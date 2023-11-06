package com.fc.mini3server.repository;

import com.fc.mini3server.domain.Dept;
import com.fc.mini3server.domain.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeptRepository extends JpaRepository<Dept, Long> {
    List<Dept> findAllByHospitalId(Long id);
    boolean existsByNameAndHospital(String dept, Hospital hospital);
}
