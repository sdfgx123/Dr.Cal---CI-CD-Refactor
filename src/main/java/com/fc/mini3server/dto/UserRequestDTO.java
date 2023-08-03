package com.fc.mini3server.dto;

import com.fc.mini3server.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@NoArgsConstructor
public class UserRequestDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class registerDTO {

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$", message = "유효하지 않은 이메일 형식입니다. 올바른 이메일 형식으로 다시 입력하십시오.")
        private String email;

        @NotBlank
        @Pattern(regexp = "^.{8,20}$", message = "비밀번호 형식이 올바르지 않습니다. 비밀번호는 8자 이상 20자 미만으로 작성하십시오.")
        private String password;

        @NotBlank
        @Pattern(regexp = "^010\\d{8}$", message = "전화번호 형식이 올바르지 않습니다. 전화번호는 하이픈(-)을 빼고 입력하십시오.")
        private String phone;

        @NotBlank
        private String name;

        @NotNull
        private Long hospital_id;

        @NotNull
        private Long dept_id;

        @Builder.Default
        private LevelEnum level = LevelEnum.INTERN;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class saveDTO {
        private String email;
        private String password;
        private String phone;
        private String name;
        private Hospital hospital;
        private Dept dept;
        private Long empNo;
        private LocalDate hireDate;
        private LevelEnum level;
        private int annual;
        private int duty;

        public User toEntity(PasswordEncoder passwordEncoder) {
            String encodedPassword = passwordEncoder.encode(this.password);
            return User.builder()
                    .empNo(empNo)
                    .hiredDate(hireDate)
                    .email(email)
                    .password(encodedPassword)
                    .phone(phone)
                    .name(name)
                    .hospital(hospital)
                    .dept(dept)
                    .level(level)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.NOTAPPROVED)
                    .annual(annual)
                    .duty(duty)
                    .build();
        }
    }

    @Getter
    public static class loginDTO {

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$", message = "유효하지 않은 이메일 형식입니다.")
        private String email;

        @NotBlank
        private String password;
    }

    @Getter
    public static class updatePasswordDTO {

        @NotBlank
        private String old_password;

        @NotBlank
        @Pattern(regexp = "^.{8,20}$", message = "비밀번호 형식이 올바르지 않습니다. 비밀번호는 8자 이상 20자 미만으로 작성하십시오.")
        private String new_password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateUserDTO {
        private String name;
        private Long deptId;
        private String phone;
        private String profileImageUrl;
    }
}
