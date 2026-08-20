package com.lms.enrollmentservice.domain.model;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReference {
    private Long courseId;
    private String instructorId;
    private long totalLessons;
    private ZonedDateTime updatedAt;

    public void syncReference(Long courseId, String instructorId, long newTotalLessons) {
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.totalLessons = newTotalLessons;
        this.updatedAt = ZonedDateTime.now();
    }
}
