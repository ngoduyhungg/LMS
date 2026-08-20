package com.lms.enrollmentservice.application.port.in;

import com.lms.enrollmentservice.domain.model.Enrollment;
import java.util.List;

public interface GetEnrollmentUseCase {
    List<Enrollment> getMyEnrollments(String userId);
    Enrollment getEnrollmentDetail(String userId, Long courseId);
}