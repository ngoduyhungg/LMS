package com.lms.courseservice.adapter.out.messaging;

import com.lms.courseservice.adapter.out.messaging.event.CourseProjectionSpringEvent;
import com.lms.courseservice.application.port.out.CourseProjectionPort;
import com.lms.courseservice.application.port.out.dto.CourseProjectionPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringEventCourseProjectionAdapter implements CourseProjectionPort {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(CourseProjectionPayload payload) {
        publisher.publishEvent(new CourseProjectionSpringEvent(payload));
    }
}