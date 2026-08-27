package com.lms.enrollmentservice.adapter.out.persistence.repository;

public interface CourseEnrollmentAggregationProjection {
    Long getCourseId();
    Long getEnrollmentCount();
    Long getActiveCount();
    Long getCompletedCount();
}
