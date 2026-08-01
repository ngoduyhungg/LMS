package com.lms.courseservice.adapter.in.rest;

import com.lms.courseservice.adapter.in.rest.dto.LessonResponse;
import com.lms.courseservice.adapter.in.rest.dto.LessonUpsertRequest;
import com.lms.courseservice.application.port.in.GetLessonUseCase;
import com.lms.courseservice.application.port.in.ManageLessonUseCase;
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
public class LessonController {
    private final GetLessonUseCase getLessonUseCase;
    private final ManageLessonUseCase manageLessonUseCase;

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessonsByModuleId(@PathVariable Long moduleId){
        return ResponseEntity.ok(ApiResponse.success("Get lessons successfully!", getLessonUseCase.getLessonsByModuleId(moduleId)));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(@PathVariable Long lessonId){
        return ResponseEntity.ok(ApiResponse.success("Get lesson details successfully!", getLessonUseCase.getLessonById(lessonId)));
    }

    @PostMapping("/modules/{moduleId}/lessons")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> addLesson(@PathVariable Long moduleId, @Valid @RequestBody LessonUpsertRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Lesson created successfully!", manageLessonUseCase.addLesson(moduleId, request)));
    }

    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(@PathVariable Long lessonId, @Valid @RequestBody LessonUpsertRequest request){
        return ResponseEntity.ok(ApiResponse.success("Lesson updated successfully!", manageLessonUseCase.updateLesson(lessonId, request)));
    }

    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable Long lessonId){
        manageLessonUseCase.deleteLesson(lessonId);
        return ResponseEntity.ok(ApiResponse.success("Lesson deleted successfully!", null));
    }

}
