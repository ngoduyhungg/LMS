package com.lms.enrollmentservice.adapter.in.rest;

import com.lms.enrollmentservice.adapter.in.rest.dto.EnrollRequest;
import com.lms.enrollmentservice.adapter.in.rest.dto.EnrollmentResponse;
import com.lms.enrollmentservice.adapter.in.rest.dto.TrackProgressRequest;
import com.lms.enrollmentservice.adapter.in.rest.mapper.EnrollmentRestMapper;
import com.lms.enrollmentservice.application.port.in.GetEnrollmentUseCase;
import com.lms.enrollmentservice.application.port.in.ManageEnrollmentUseCase;
import com.lms.enrollmentservice.domain.model.Enrollment;
import com.lms.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final ManageEnrollmentUseCase manageEnrollmentUseCase;
    private final EnrollmentRestMapper restMapper;
    private final GetEnrollmentUseCase getEnrollmentUseCase;

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();

        var command = restMapper.toCommand(request, currentUserId);
        Enrollment enrollment = manageEnrollmentUseCase.enrollUser(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(restMapper.toResponse(enrollment));
    }

    @PutMapping("/courses/{courseId}/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> trackProgress(
            @PathVariable Long courseId,
            @Valid @RequestBody TrackProgressRequest request) {

        String currentUserId = SecurityUtils.getCurrentUserId();

        var command = restMapper.toCommand(request, currentUserId, courseId);
        Enrollment updatedEnrollment = manageEnrollmentUseCase.trackLessonProgress(command);

        return ResponseEntity.ok(restMapper.toResponse(updatedEnrollment));
    }
    @GetMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<EnrollmentResponse>> getMyEnrollments() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        List<Enrollment> enrollments = getEnrollmentUseCase.getMyEnrollments(currentUserId);
        return ResponseEntity.ok(restMapper.toResponseList(enrollments));
    }

    @GetMapping("/courses/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<EnrollmentResponse> getEnrollmentDetail(@PathVariable Long courseId) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        Enrollment enrollment = getEnrollmentUseCase.getEnrollmentDetail(currentUserId, courseId);
        return ResponseEntity.ok(restMapper.toResponse(enrollment));
    }
}
