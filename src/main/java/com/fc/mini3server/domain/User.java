package com.fc.mini3server.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Entity(name = "user_tb")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long empNo;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, unique = true, length = 40)
    private String email;

    @Column(nullable = false, length = 120)
    private String password;

    @Column(length = 11)
    private String phone;

    @ManyToOne(optional = false)
    private Hospital hospital;

    @ManyToOne(optional = false)
    private Dept dept;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private LevelEnum level;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private AuthEnum auth;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private StatusEnum status;

    private int annual;

    private int duty;

    private String profileImageUrl;

    private LocalDate hiredDate;

    private LocalDate resignedDate;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void updateAuth(AuthEnum auth){
        this.auth = auth;
    }

    public void updateStatus(StatusEnum status) {
        this.status = status;
    }

    public void changePassword(String newPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(newPassword);
    }

    public void usedAnnual(int newAnnual) {
        this.annual -= newAnnual + 1;
    }

    public void approvedDuty() {
        this.duty = duty - 1;
    }
}
