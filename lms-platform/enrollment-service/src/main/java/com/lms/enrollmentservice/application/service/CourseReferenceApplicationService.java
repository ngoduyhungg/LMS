package com.lms.enrollmentservice.application.service;

import com.lms.enrollmentservice.application.port.in.SyncCourseReferenceUseCase;
import com.lms.enrollmentservice.application.port.out.CourseReferenceRepositoryPort;
import com.lms.enrollmentservice.domain.model.CourseReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseReferenceApplicationService implements SyncCourseReferenceUseCase {

    private final CourseReferenceRepositoryPort repositoryPort;

    @Override
    public void syncReference(Long courseId, String instructorId, long totalLessons) {
        CourseReference reference = repositoryPort.findByCourseId(courseId)
                .orElseGet(() -> CourseReference.builder().courseId(courseId).build());

        // Domain Model tự cập nhật data và timestamp
        reference.syncReference(courseId, instructorId, totalLessons);

        // [LOG B]
        log.info(
                "Saving CourseReference - courseId: {}, instructorId: {}, totalLessons: {}",
                courseId,
                instructorId,
                totalLessons
        );

        repositoryPort.save(reference);

        // [LOG C]
        log.info("CourseReference synchronized successfully for courseId: {}", courseId);
    }
}