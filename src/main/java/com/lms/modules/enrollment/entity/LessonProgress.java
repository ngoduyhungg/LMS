package com.lms.modules.enrollment.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.course.entity.Lesson;
import com.lms.modules.enrollment.enums.LessonProgressStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Table(name = "lesson_progress", uniqueConstraints = {@UniqueConstraint(name = "uk_lesson_progress_enrollment_lesson", columnNames = {"enrollment_id", "lesson_id"})})
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LessonProgressStatus status = LessonProgressStatus.IN_PROGRESS;
    @Column(name = "watched_seconds")
    @Builder.Default
    private Integer watchedSeconds = 0;
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
}
