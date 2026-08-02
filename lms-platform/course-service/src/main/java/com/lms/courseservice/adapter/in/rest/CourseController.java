package com.lms.courseservice.adapter.in.rest;

import com.lms.courseservice.adapter.in.rest.dto.*;
import com.lms.courseservice.adapter.in.rest.mapper.CourseRestMapper;
import com.lms.courseservice.application.port.in.*;
import com.lms.courseservice.application.port.in.command.CourseCommand;
import com.lms.courseservice.domain.model.Course;
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
    private final CourseRestMapper restMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAll() {
        List<Course> courses = getCourseUseCase.getAllPublishedCourses();
        return ResponseEntity.ok(ApiResponse.success("Get list successfully!", restMapper.toResponseList(courses)));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CourseResponse>> getBySlug(@PathVariable String slug) {
        Course course = getCourseUseCase.getCourseDetail(slug);
        return ResponseEntity.ok(ApiResponse.success(restMapper.toResponse(course)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CourseUpsertRequest request) {
        CourseCommand command = restMapper.toCommand(request);
        Course course = manageCourseUseCase.createCourse(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(restMapper.toResponse(course)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseUpsertRequest request) {
        CourseCommand command = restMapper.toCommand(request);
        Course course = manageCourseUseCase.updateCourse(id, command);
        return ResponseEntity.ok(ApiResponse.success(restMapper.toResponse(course)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        manageCourseUseCase.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}/curriculum")
    public ResponseEntity<ApiResponse<CourseCurriculumResponse>> getCurriculum(@PathVariable Long id) {
        Course course = getCourseUseCase.getCurriculum(id);
        return ResponseEntity.ok(ApiResponse.success(restMapper.toCurriculumResponse(course)));
    }
}