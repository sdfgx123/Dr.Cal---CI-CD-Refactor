package com.fc.mini3server.dto;

import com.fc.mini3server.domain.Hospital;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class HospitalResponseDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class HospitalListDTO {
        private Long hospitalId;
        private String hospitalName;

        public static HospitalListDTO of(Hospital hospital) {
            return new HospitalListDTO(hospital.getId(), hospital.getName());
        }

        public static List<HospitalListDTO> listOf(List<Hospital> hospitalList){
            return hospitalList.stream().map(HospitalListDTO::of).collect(Collectors.toList());
        }
    }
}
