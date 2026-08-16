package com.lms.enrollmentservice.adapter.in.rest.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.ZonedDateTime;

@Getter
@Builder
public class LessonProgressResponse {
    private Long lessonId;
    private String status;
    private int watchedSeconds;
    private ZonedDateTime completedAt;
}