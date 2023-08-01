package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server.domain.Dept;
import com.fc.mini3server.repository.DeptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeptService {
    private final DeptRepository deptRepository;

    public List<Dept> findAll(Long id) {
        deptRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER)
        );

        return deptRepository.findAllByHospitalId(id);
    }
}
