package com.lms.courseservice.adapter.out.messaging.event;

import com.lms.courseservice.application.port.out.dto.CourseProjectionPayload;

public record CourseProjectionSpringEvent(CourseProjectionPayload payload) {}