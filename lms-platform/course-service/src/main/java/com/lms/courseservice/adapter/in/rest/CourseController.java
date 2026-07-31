package com.lms.courseservice.adapter.in.rest;

import com.lms.courseservice.adapter.in.rest.dto.*;
import com.lms.courseservice.application.port.in.*;
import com.lms.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final GetCourseUseCase getCourseUseCase;
    private final ManageCourseUseCase manageCourseUseCase;

    // =========================================================
    // COURSE ENDPOINTS
    // =========================================================
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Get list successfully!", getCourseUseCase.getAllPublishedCourses()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CourseResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(getCourseUseCase.getCourseDetail(slug)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CourseUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(manageCourseUseCase.createCourse(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseUpsertRequest request) {
        return ResponseEntity.ok(ApiResponse.success(manageCourseUseCase.updateCourse(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        manageCourseUseCase.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}/curriculum")
    public ResponseEntity<ApiResponse<CourseCurriculumResponse>> getCurriculum(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(getCourseUseCase.getCurriculum(id)));
    }


}