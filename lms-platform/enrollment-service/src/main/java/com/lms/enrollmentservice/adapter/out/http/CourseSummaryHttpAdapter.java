package com.lms.enrollmentservice.adapter.out.http;

import com.lms.enrollmentservice.application.port.out.CourseSummaryPort;
import com.lms.enrollmentservice.application.port.out.dto.CourseSummary;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseSummaryHttpAdapter implements CourseSummaryPort {

    private final RestClient courseServiceClient;

    @Override
    public CourseSummary getCourseSummary(Long courseId) {
        try {
            return courseServiceClient.get()
                    .uri("/api/internal/courses/{courseId}/summary", courseId)
                    .retrieve()
                    .body(CourseSummary.class);
        } catch (RestClientResponseException e) {
            log.error("Failed to fetch course summary from course-service for courseId: {}. Status: {}, Body: {}",
                    courseId, e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 404) {
                throw new BusinessException(ErrorCode.COURSE_NOT_FOUND);
            }
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Network error when calling course-service", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}