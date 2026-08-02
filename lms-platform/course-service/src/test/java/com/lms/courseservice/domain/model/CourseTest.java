package com.lms.courseservice.domain.model;

import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CourseTest {

    @Test
    @DisplayName("Publish Course thất bại - Quăng lỗi nếu không có Module nào")
    void should_ThrowException_When_PublishCourse_WithNoModules() {
        Category category = Category.builder().build();
        Course course = Course.create("Title", "slug", "Summary", "Desc", null, null, "thumb.jpg", category, "instructor-1");

        // FIX: Xác minh trực tiếp bằng mã ErrorCode thay vì chuỗi văn bản (String)
        assertThatThrownBy(course::publish)
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException bizEx = (BusinessException) ex;
                    assertThat(bizEx.getErrorCode()).isEqualTo(ErrorCode.COURSE_HAS_NO_MODULES);
                });
    }

    @Test
    @DisplayName("Publish Course thành công - Đổi trạng thái sang PUBLISHED")
    void should_PublishCourse_Successfully() {
        // Chuẩn bị dữ liệu đầy đủ
        Category category = Category.builder().build();
        Course course = Course.create("Title", "slug", "Summary", "Desc", null, null, "thumb.jpg", category, "instructor-1");

        Module module = Module.create(course, "Module 1", 1);
        Lesson lesson = Lesson.builder().title("Lesson 1").build();
        module.getLessons().add(lesson);
        course.getModules().add(module);

        // Hành động
        course.publish();

        // Kiểm chứng
        assertThat(course.getStatus()).isEqualTo(CourseStatus.PUBLISHED);
    }
}