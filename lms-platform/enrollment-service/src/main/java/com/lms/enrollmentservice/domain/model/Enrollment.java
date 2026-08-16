package com.lms.enrollmentservice.domain.model;

import com.lms.enrollmentservice.domain.enums.EnrollmentStatus;
import com.lms.enrollmentservice.domain.enums.LessonProgressStatus;
import com.lms.enrollmentservice.domain.shared.AuditInfo;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    private Long id;
    private String userId;
    private Long courseId;
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
    @Builder.Default
    private BigDecimal progressPercentage = BigDecimal.ZERO;
    private Long lastAccessedLessonId;
    private ZonedDateTime enrolledAt;
    private ZonedDateTime completedAt;
    private ZonedDateTime expiresAt;

    @Builder.Default
    private List<LessonProgress> lessonProgresses = new ArrayList<>();

    private AuditInfo auditInfo;

    public void checkCanTrackProgress() {
        if (this.status != EnrollmentStatus.ACTIVE) {
            // Không import ErrorCode vào Domain, có thể ném RuntimeException nội bộ
            // và để ApplicationService map, HOẶC ném BusinessException nếu Domain của bạn cho phép.
            // Dựa trên pattern hiện tại:
            throw new com.lms.shared.exception.BusinessException(com.lms.shared.enums.ErrorCode.ENROLLMENT_NOT_ACTIVE);
        }
    }

    public void recordLessonProgress(Long lessonId, int watchedSeconds, boolean isCompleted, long currentTotalCourseLessons) {
        LessonProgress targetLesson = this.lessonProgresses.stream()
                .filter(lp -> lp.getLessonId().equals(lessonId))
                .findFirst()
                .orElseGet(() -> {
                    LessonProgress newProgress = LessonProgress.builder()
                            .lessonId(lessonId)
                            .status(LessonProgressStatus.IN_PROGRESS)
                            .watchedSeconds(0)
                            .build();
                    this.lessonProgresses.add(newProgress);
                    return newProgress;
                });

        targetLesson.updateProgress(watchedSeconds, isCompleted);
        this.lastAccessedLessonId = lessonId;

        recalculateProgress(currentTotalCourseLessons);
    }

    public void recalculateProgress(long currentTotalCourseLessons) {
        if (currentTotalCourseLessons <= 0) {
            this.progressPercentage = BigDecimal.ZERO;
            return;
        }

        long completedLessons = this.lessonProgresses.stream()
                .filter(lp -> lp.getStatus() == LessonProgressStatus.COMPLETED)
                .count();

        // Tiến độ động: Tính trên currentTotalCourseLessons
        BigDecimal percentage = BigDecimal.valueOf((double) completedLessons / currentTotalCourseLessons * 100)
                .setScale(2, RoundingMode.HALF_UP);

        this.progressPercentage = percentage.min(new BigDecimal("100.00"));

        checkCompletionRules();
    }

    private void checkCompletionRules() {
        if (this.progressPercentage.compareTo(new BigDecimal("100.00")) == 0) {
            this.status = EnrollmentStatus.COMPLETED;
            if (this.completedAt == null) {
                this.completedAt = ZonedDateTime.now();
            }
        } else {
            // Hạ status xuống ACTIVE nếu tiến độ tụt (Do giảng viên thêm bài mới)
            this.status = EnrollmentStatus.ACTIVE;
        }
    }
    public void cancel() {
        this.status = EnrollmentStatus.CANCELLED;
    }
}
