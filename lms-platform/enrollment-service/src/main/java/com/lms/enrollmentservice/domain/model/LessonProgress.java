package com.lms.enrollmentservice.domain.model;

import com.lms.enrollmentservice.domain.enums.LessonProgressStatus;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress {
    private Long id;
    private Long lessonId;
    private LessonProgressStatus status;
    private int watchedSeconds;
    private ZonedDateTime completedAt;

    public void updateProgress(int watchedSeconds, boolean isCompleted) {
        this.watchedSeconds = watchedSeconds;
        if (isCompleted && this.status != LessonProgressStatus.COMPLETED) {
            this.status = LessonProgressStatus.COMPLETED;
            this.completedAt = ZonedDateTime.now();
        }
    }
}
