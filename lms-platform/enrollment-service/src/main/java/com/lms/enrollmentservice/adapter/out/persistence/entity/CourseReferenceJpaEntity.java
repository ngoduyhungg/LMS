package com.lms.enrollmentservice.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "course_references")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReferenceJpaEntity {

    @Id
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "instructor_id", nullable = false)
    private String instructorId;

    @Column(name = "total_lessons", nullable = false)
    @Builder.Default
    private Long totalLessons = 0L;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}
