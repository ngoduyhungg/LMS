package com.lms.modules.course.controller;

import com.lms.common.response.ApiResponse;
import com.lms.modules.course.dto.*;
import com.lms.modules.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAll(){
        return ResponseEntity.ok(ApiResponse.success("Get list successfully!", courseService.getAllPublishedCourses()));
    }
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CourseResponse>> getBySlug(@PathVariable String slug){
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseDetail(slug)));
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CourseUpsertRequest request,
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        // Giả định UserDetails hoặc SecurityContext holder lấy được user/instructor Id
        // Thay thế logic lấy id phù hợp với implementation Jwt hiện tại của bạn:
        Long instructorId = Long.valueOf(userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseService.createCourse(request, instructorId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseUpsertRequest request) {
        return ResponseEntity.ok(ApiResponse.success(courseService.updateCourse(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}/curriculum")
    public ResponseEntity<ApiResponse<CourseCurriculumResponse>> getCurriculum(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCurriculum(id)));
    }

    @PostMapping("/{courseId}/modules")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<ModuleResponse>> addModule(
            @PathVariable Long courseId,
            @Valid @RequestBody ModuleUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseService.addModule(courseId, request)));
    }

    @PutMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(
            @PathVariable Long moduleId,
            @Valid @RequestBody ModuleUpsertRequest request) {
        return ResponseEntity.ok(ApiResponse.success(courseService.updateModule(moduleId, request)));
    }

    @DeleteMapping("/modules/{moduleId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteModule(@PathVariable Long moduleId) {
        courseService.deleteModule(moduleId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/modules/{moduleId}/lessons")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> addLesson(
            @PathVariable Long moduleId,
            @Valid @RequestBody LessonUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(courseService.addLesson(moduleId, request)));
    }

    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable Long lessonId,
            @Valid @RequestBody LessonUpsertRequest request) {
        return ResponseEntity.ok(ApiResponse.success(courseService.updateLesson(lessonId, request)));
    }

    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable Long lessonId) {
        courseService.deleteLesson(lessonId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
