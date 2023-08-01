package com.fc.mini3server.dto;

import com.fc.mini3server.domain.AuthEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AdminRequestDTO {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class editAuthDTO {
        private AuthEnum auth;
    }
}
