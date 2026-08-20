package com.lms.enrollmentservice.adapter.in.messaging.dto;

import lombok.Data;

@Data
public class CourseReferenceEvent {
    Long courseId;
    String instructorId;
    long totalLessons;
}
