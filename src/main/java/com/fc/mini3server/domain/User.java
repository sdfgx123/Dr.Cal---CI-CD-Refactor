package com.fc.mini3server.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
    private Hospital hospitalId;

    @ManyToOne(optional = false)
    private Dept deptId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Auth auth;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private int annual;

    private int duty;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String profileImageUrl;

    private LocalDate hiredDate;

    private LocalDate resignedDate;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
