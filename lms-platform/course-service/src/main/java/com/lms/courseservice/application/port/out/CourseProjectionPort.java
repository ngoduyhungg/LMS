package com.lms.courseservice.application.port.out;
import com.lms.courseservice.application.port.out.dto.CourseProjectionPayload;

public interface CourseProjectionPort {
    void publish(CourseProjectionPayload payload);
}