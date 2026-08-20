package com.lms.enrollmentservice.adapter.out.persistence.entity;

import com.lms.enrollmentservice.domain.enums.LessonProgressStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private EnrollmentJpaEntity enrollment;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private LessonProgressStatus status = LessonProgressStatus.IN_PROGRESS;

    @Builder.Default
    @Column(name = "watched_seconds", nullable = false)
    private Integer watchedSeconds = 0;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;
}
