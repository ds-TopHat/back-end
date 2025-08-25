package com.mathfusion.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // User와 1:1 매핑 가능 (필요하면 @OneToOne)

    @Column(nullable = false, unique = true, length = 500)
    private String token;
}
