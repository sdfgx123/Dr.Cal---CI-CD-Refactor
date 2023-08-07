package com.fc.mini3server.dto;

import com.fc.mini3server.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserResponseDTO {

    private Long id;
    private Long emp_no;
    private String name;
    private String email;
    private String phone;
    private Long hospital_id;
    private Long dept_id;
    private String level;
    private String auth;
    private String status;
    private int annual;
    private int duty;
    private String profile_image_url;
    private LocalDate hiredate;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;



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
