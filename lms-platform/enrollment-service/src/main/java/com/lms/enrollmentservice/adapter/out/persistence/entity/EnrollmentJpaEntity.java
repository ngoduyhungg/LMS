package com.lms.enrollmentservice.adapter.out.persistence.entity;

import com.lms.enrollmentservice.domain.enums.EnrollmentStatus;
import com.lms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentJpaEntity extends AuditableEntity {

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Builder.Default
    @Column(name = "progress_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal progressPercentage = BigDecimal.ZERO;

    @Column(name = "last_accessed_lesson_id")
    private Long lastAccessedLessonId;

    @Builder.Default
    @Column(name = "enrolled_at", nullable = false)
    private ZonedDateTime enrolledAt = ZonedDateTime.now();

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Column(name = "expires_at")
    private ZonedDateTime expiresAt;

    // CASCADE: Khi lưu Enrollment sẽ tự động lưu hoặc cập nhật LessonProgress
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<LessonProgressJpaEntity> lessonProgresses = new ArrayList<>();
}