package com.lms.modules.enrollment.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.course.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "course_progress")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseProgress extends AuditableEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;
    @Column(name = "progress_percentage", precision = 5, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal progressPercentage = new BigDecimal("0.00");
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_accessed_lesson_id")
    private Lesson lesson;
}
