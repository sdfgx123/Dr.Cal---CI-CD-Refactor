package com.fc.mini3server.dto;

import com.fc.mini3server.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private int annual;
    private int duty;

    public static UserResponseDTO of(User user){
        if (user == null) {
            return new UserResponseDTO();
        }
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getAnnual(), user.getDuty());
    }

    public static List<UserResponseDTO> listOf(List<User> users){
        return users.stream().map(UserResponseDTO::of).collect(Collectors.toList());
    }
}
