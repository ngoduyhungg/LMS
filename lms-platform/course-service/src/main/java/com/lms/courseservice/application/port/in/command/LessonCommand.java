package com.lms.courseservice.application.port.in.command;

import com.lms.courseservice.domain.enums.LessonType;

import java.util.List;

public record LessonCommand(String title, String content, String videoUrl, Integer durationSeconds, LessonType lessonType, Boolean isPreview, Integer sortOrder, List<LessonResourceCommand> resources) {
    public record LessonResourceCommand(String title, String fileUrl, String fileType, Long fileSizeBytes) {}
}
