package com.mathfusion.domain.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ai_job")
public class AiJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AiJobStatus status;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String requestJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String resultJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private int retryCount;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @Builder
    public AiJob(String userEmail, String requestJson) {
        this.userEmail = userEmail;
        this.requestJson = requestJson;
        this.status = AiJobStatus.PENDING;
        this.retryCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void start() {
        this.status = AiJobStatus.PROCESSING;
        this.startedAt = LocalDateTime.now();
    }

    public void complete(String resultJson) {
        this.status = AiJobStatus.DONE;
        this.resultJson = resultJson;
        this.finishedAt = LocalDateTime.now();
    }

    public void fail(String errorMessage) {
        this.status = AiJobStatus.FAILED;
        this.errorMessage = errorMessage;
        this.finishedAt = LocalDateTime.now();
    }

    public void increaseRetryCount() {
        this.retryCount++;
    }
}