package com.lms.modules.quiz.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.auth.entity.User;
import com.lms.modules.enrollment.entity.Enrollment;
import com.lms.modules.quiz.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Table(name = "quiz_submissions", uniqueConstraints = {@UniqueConstraint(name = "uk_quiz_submissions_attempt", columnNames = {"quiz_id", "user_id", "attempt_number"})})
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmission extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;
    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;
    @Column(name = "total_score", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalScore = new BigDecimal("0.00");
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status = SubmissionStatus.IN_PROGRESS;
    @Column(name = "started_at", nullable = false)
    @Builder.Default
    private OffsetDateTime startedAt = OffsetDateTime.now();
    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;
}
