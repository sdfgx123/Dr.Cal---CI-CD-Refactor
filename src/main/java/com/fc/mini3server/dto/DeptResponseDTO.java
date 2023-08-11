package com.fc.mini3server.dto;

import com.fc.mini3server.domain.Dept;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class DeptResponseDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class DeptListDTO {

        private Long hospitalId;
        private Long deptId;
        private String deptName;

        public static DeptListDTO of(Dept dept) {
            return new DeptListDTO(dept.getHospital().getId(), dept.getId(), dept.getName());
        }

        public static List<DeptListDTO> listOf(List<Dept> deptList){
            return deptList.stream().map(DeptListDTO::of).collect(Collectors.toList());
        }
    }
}
