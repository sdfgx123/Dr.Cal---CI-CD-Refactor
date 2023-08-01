package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.fc.mini3server.dto.HospitalResponseDTO.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/hospital")
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping("/list")
    public ResponseEntity<?> findAll(){
        final List<Hospital> hospitalList = hospitalService.findAll();
        return ResponseEntity.ok(ApiUtils.success(HospitalListDTO.listOf(hospitalList)));
    }
}
