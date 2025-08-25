package com.mathfusion.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String code; //인증번호

    @Column(nullable = false)
    private LocalDateTime expiredTime; //만료시간

    @Column(nullable = false)
    private boolean verified; //인증 완료 여부
}