package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.Dept;
import com.fc.mini3server.service.DeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.fc.mini3server.dto.DeptResponseDTO.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dept")
public class DeptController {
    private final DeptService deptService;

    @GetMapping("/list")
    public ResponseEntity<?> findAll(){
        final List<Dept> deptList = deptService.findAll();
        return ResponseEntity.ok(ApiUtils.success(DeptListDTO.listOf(deptList)));
    }
}
