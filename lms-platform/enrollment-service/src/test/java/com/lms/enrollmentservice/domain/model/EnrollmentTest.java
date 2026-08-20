package com.lms.enrollmentservice.domain.model;

import com.lms.enrollmentservice.domain.enums.EnrollmentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class EnrollmentTest {

    @Test
    @DisplayName("Nên khởi tạo Enrollment với 0% tiến độ và trạng thái ACTIVE")
    void shouldInitializeWithZeroProgressAndActiveStatus() {
        // Arrange & Act
        Enrollment enrollment = Enrollment.builder()
                .userId("student-1")
                .courseId(1L)
                .build();

        // Assert
        assertThat(enrollment.getProgressPercentage()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(enrollment.getLessonProgresses()).isEmpty();
    }

    @Test
    @DisplayName("Nên tính toán đúng % khi cập nhật tiến độ bài học")
    void shouldCalculateProgressCorrectlyWhenTrackingLesson() {
        // Arrange
        Enrollment enrollment = Enrollment.builder().courseId(1L).build();

        // Act: Hoàn thành 1 bài trong tổng số 4 bài (Kỳ vọng: 25%)
        enrollment.recordLessonProgress(101L, 300, true, 4);

        // Assert
        assertThat(enrollment.getProgressPercentage()).isEqualByComparingTo(new BigDecimal("25.00"));
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(enrollment.getLessonProgresses()).hasSize(1);
    }

    @Test
    @DisplayName("Nên đổi trạng thái thành COMPLETED khi đạt 100% tiến độ")
    void shouldMarkAsCompletedWhenAllLessonsFinished() {
        // Arrange
        Enrollment enrollment = Enrollment.builder().courseId(1L).build();

        // Act: Hoàn thành 2 bài trong tổng số 2 bài (Kỳ vọng: 100%)
        enrollment.recordLessonProgress(101L, 300, true, 2);
        enrollment.recordLessonProgress(102L, 400, true, 2);

        // Assert
        assertThat(enrollment.getProgressPercentage()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("Nên tụt tiến độ và về ACTIVE khi tổng số bài học tăng lên (Rule 3 P1)")
    void shouldRevertToActiveWhenTotalLessonsIncrease() {
        // Arrange: Đã hoàn thành 1/1 bài -> 100% COMPLETED
        Enrollment enrollment = Enrollment.builder().courseId(1L).build();
        enrollment.recordLessonProgress(101L, 300, true, 1);
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);

        // Act: Tổng số bài học tăng lên thành 2 (Giảng viên thêm bài mới)
        enrollment.recalculateProgress(2);

        // Assert: Tiến độ phải tụt về 50% và mất trạng thái COMPLETED
        assertThat(enrollment.getProgressPercentage()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    @DisplayName("Nên đổi trạng thái sang CANCELLED khi Admin cưỡng chế hủy")
    void shouldCancelEnrollment() {
        // Arrange
        Enrollment enrollment = Enrollment.builder().courseId(1L).build();

        // Act
        enrollment.cancel();

        // Assert
        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
    }
}