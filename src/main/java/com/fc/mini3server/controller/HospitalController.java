package com.fc.mini3server.controller;

import com.fc.mini3server._core.utils.ApiUtils;
import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.fc.mini3server.dto.HospitalResponseDTO.*;

@Tag(name = "Hospital", description = "병원 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/hospital")
public class HospitalController {

    private final HospitalService hospitalService;

    @Operation(summary = "병원 정보 리스트")
    @GetMapping()
    public ResponseEntity<?> findAll(){
        final List<Hospital> hospitalList = hospitalService.findAll();
        return ResponseEntity.ok(ApiUtils.success(HospitalListDTO.listOf(hospitalList)));
    }
}
