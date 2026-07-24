package com.lms.modules.quiz.entity;

import com.lms.common.entity.AuditableEntity;
import com.lms.modules.course.entity.Course;
import com.lms.modules.course.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "quizzes")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @Column(name = "title", length = 255, nullable = false)
    private String title;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;
    @Column(name = "passing_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal passingScore = new BigDecimal("70.00");
    @Column(name = "max_attempts")
    @Builder.Default
    private Integer maxAttempts = 3;

}
