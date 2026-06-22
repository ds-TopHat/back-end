package com.mathfusion.domain.ai.entity;

import com.mathfusion.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AiJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_job_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AiJobStatus status;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String downloadUrlsJson;

    @Column(name = "s3_key")
    private String s3Key;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String resultJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void markProcessing() {
        this.status = AiJobStatus.PROCESSING;
        this.errorMessage = null;
    }

    public void markDone(String resultJson) {
        this.status = AiJobStatus.DONE;
        this.resultJson = resultJson;
        this.errorMessage = null;
    }

    public void markFailed(String errorMessage) {
        this.status = AiJobStatus.FAILED;
        this.errorMessage = errorMessage;
    }
}
