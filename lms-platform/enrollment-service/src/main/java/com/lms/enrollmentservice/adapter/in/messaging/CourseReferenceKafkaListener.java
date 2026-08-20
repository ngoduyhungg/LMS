package com.lms.enrollmentservice.adapter.in.messaging;

import com.lms.enrollmentservice.adapter.in.messaging.dto.CourseReferenceEvent;
import com.lms.enrollmentservice.application.port.in.SyncCourseReferenceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CourseReferenceKafkaListener {

    private final SyncCourseReferenceUseCase syncCourseReferenceUseCase;

    @KafkaListener(
            topics = "course.projection.events",
            groupId = "enrollment-service-group"
    )
    public void handleCourseReferencesEvent(CourseReferenceEvent event) {
        log.info(
                "Received CourseReferenceEvent for courseId: {}, instructorId: {}, totalLessons: {}",
                event.getCourseId(),
                event.getInstructorId(),
                event.getTotalLessons()
        );

        syncCourseReferenceUseCase.syncReference(
                event.getCourseId(),
                event.getInstructorId(),
                event.getTotalLessons()
        );

        log.info(
                "Successfully synced references for course {}",
                event.getCourseId()
        );
    }
}