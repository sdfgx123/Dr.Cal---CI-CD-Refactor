package com.fc.mini3server.service;

import com.fc.mini3server.domain.Dept;
import com.fc.mini3server.repository.DeptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeptService {
    private final DeptRepository deptRepository;

    public List<Dept> findAll() {
        return deptRepository.findAll();
    }
}
