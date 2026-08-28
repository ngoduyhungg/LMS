package com.lms.enrollmentservice.adapter.in.rest;

import com.lms.enrollmentservice.adapter.in.rest.dto.AdminCourseEnrollmentSummaryResponse;
import com.lms.enrollmentservice.adapter.in.rest.dto.EnrollmentResponse;
import com.lms.enrollmentservice.adapter.in.rest.mapper.EnrollmentRestMapper;
import com.lms.enrollmentservice.application.port.in.ManageAdminEnrollmentUseCase;
import com.lms.enrollmentservice.domain.model.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/enrollments")
@RequiredArgsConstructor
public class AdminEnrollmentController {

    private final ManageAdminEnrollmentUseCase manageAdminEnrollmentUseCase;
    private final EnrollmentRestMapper restMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        List<Enrollment> enrollments = manageAdminEnrollmentUseCase.getAllEnrollments();
        return ResponseEntity.ok(restMapper.toResponseList(enrollments));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnrollmentResponse> forceCancelEnrollment(@PathVariable Long id) {
        Enrollment canceledEnrollment = manageAdminEnrollmentUseCase.forceCancelEnrollment(id);
        return ResponseEntity.ok(restMapper.toResponse(canceledEnrollment));
    }
    @GetMapping("/courses/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminCourseEnrollmentSummaryResponse>> getCourseEnrollmentSummaries() {
        return ResponseEntity.ok(manageAdminEnrollmentUseCase.getCourseEnrollmentSummaries());
    }
}