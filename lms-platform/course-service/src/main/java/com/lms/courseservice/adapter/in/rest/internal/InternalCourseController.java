package com.lms.courseservice.adapter.in.rest.internal;

import com.lms.courseservice.adapter.in.rest.dto.internal.CourseSummaryResponse;
import com.lms.courseservice.adapter.in.rest.dto.internal.LessonValidationResponse;
import com.lms.courseservice.application.port.in.GetCourseSummaryUseCase;
import com.lms.courseservice.application.port.in.ValidateLessonUseCase;
import com.lms.courseservice.domain.model.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal/courses")
@RequiredArgsConstructor
public class InternalCourseController {

    private final ValidateLessonUseCase validateLessonUseCase;
    private final GetCourseSummaryUseCase getCourseSummaryUseCase;

    @GetMapping("/{courseId}/lessons/{lessonId}/validate")
    public ResponseEntity<LessonValidationResponse> validateLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId) {
        boolean isValid = validateLessonUseCase.validateLessonInCourse(courseId, lessonId);
        return ResponseEntity.ok(new LessonValidationResponse(isValid));
    }

    @GetMapping("/{courseId}/summary")
    public ResponseEntity<CourseSummaryResponse> getCourseSummary(@PathVariable Long courseId) {
        Course course = getCourseSummaryUseCase.getCourseSummary(courseId);
        return ResponseEntity.ok(new CourseSummaryResponse(course.getId(), course.getTitle(), course.getStatus()));
    }
}