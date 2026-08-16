package com.lms.enrollmentservice.adapter.out.http;

import com.lms.enrollmentservice.application.port.out.CourseValidationPort;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseValidationHttpAdapter implements CourseValidationPort {

    private final RestClient courseServiceClient;

    @Override
    @SuppressWarnings("unchecked")
    public boolean isLessonValidForCourse(Long courseId, Long lessonId) {
        try {
            Map<String, Boolean> response = courseServiceClient.get()
                    .uri("/api/internal/courses/{courseId}/lessons/{lessonId}/validate", courseId, lessonId)
                    .retrieve()
                    .body(Map.class);
            return response != null && Boolean.TRUE.equals(response.get("isValid"));
        } catch (RestClientResponseException e) {
            log.error("Failed to validate lesson from course-service. CourseId: {}, LessonId: {}. Status: {}",
                    courseId, lessonId, e.getStatusCode());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Network error when validating lesson with course-service", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}