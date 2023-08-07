package com.fc.mini3server.dto;

import com.fc.mini3server.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserResponseDTO {

    private Long id;
    private Long empNo;
    private String name;
    private String email;
    private String phone;
    private Long hospitalId;
    private Long deptId;
    private String level;
    private String auth;
    private String status;
    private int annual;
    private int duty;
    private String profileImageUrl;
    private LocalDate hireDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    public static UserResponseDTO of(User user){
        if (user == null) {
            return new UserResponseDTO();
        }
        return new UserResponseDTO(
                user.getId(),
                user.getEmpNo(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getHospital().getId(),
                user.getDept().getId(),
                user.getLevel().name(),
                user.getAuth().name(),
                user.getStatus().name(),
                user.getAnnual(),
                user.getDuty(),
                user.getProfileImageUrl(),
                user.getHiredDate(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
