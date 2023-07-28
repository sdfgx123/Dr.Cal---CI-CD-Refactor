package com.fc.mini3server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


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
}
