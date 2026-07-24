package com.lms.modules.enrollment.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.enrollment.enums.EnrollmentStatus;
import com.lms.modules.auth.entity.User;
import com.lms.modules.course.entity.Course;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Table(name = "enrollments", uniqueConstraints = {@UniqueConstraint(name = "uk_enrollment_user_course", columnNames = {"user_id", "course_id"})})
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
    @Column(name = "enrolled_at", nullable = false)
    @Builder.Default
    private OffsetDateTime enrolledAt = OffsetDateTime.now();
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;
}
