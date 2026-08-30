package com.lms.enrollmentservice.application.port.in;

import com.lms.enrollmentservice.adapter.in.rest.dto.AdminCourseEnrollmentSummaryResponse;
import com.lms.enrollmentservice.adapter.in.rest.dto.AdminStudentEnrollmentResponse;
import com.lms.enrollmentservice.adapter.in.rest.dto.PageResponse;
import com.lms.enrollmentservice.domain.model.Enrollment;
import java.util.List;

public interface ManageAdminEnrollmentUseCase {
    List<Enrollment> getAllEnrollments();
    Enrollment forceCancelEnrollment(Long enrollmentId);
    List<AdminCourseEnrollmentSummaryResponse> getCourseEnrollmentSummaries();
    PageResponse<AdminStudentEnrollmentResponse> getStudentEnrollmentsByCourse(Long courseId, int page, int size);
}