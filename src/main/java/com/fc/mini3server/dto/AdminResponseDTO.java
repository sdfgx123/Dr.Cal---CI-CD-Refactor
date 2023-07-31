package com.fc.mini3server.dto;

import com.fc.mini3server.domain.AuthEnum;
import com.fc.mini3server.domain.LevelEnum;
import com.fc.mini3server.domain.StatusEnum;
import com.fc.mini3server.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

public class AdminResponseDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AdminUserListDTO {
        private Long id;
        private String username;
        private String phone;
        private String hospitalName;
        private String deptName;
        private LevelEnum level;
        private AuthEnum auth;
        private StatusEnum status;

        public static AdminUserListDTO of(User user) {
            return new AdminUserListDTO(user.getId(), user.getName(), user.getPhone(), user.getHospital().getName(),
                    user.getDept().getName(), user.getLevel(), user.getAuth(), user.getStatus());
        }

        public static List<AdminUserListDTO> listOf(List<User> users){
            return users.stream().map(AdminUserListDTO::of).collect(Collectors.toList());
        }
    }
}
