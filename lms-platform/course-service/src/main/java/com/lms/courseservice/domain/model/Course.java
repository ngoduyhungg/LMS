package com.lms.courseservice.domain.model;

import com.lms.courseservice.domain.enums.CourseLevel;
import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.courseservice.domain.shared.AuditInfo;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    private Long id;
    private String instructor;
    private Category category;
    private String title;
    private String slug;
    private String summary;
    private String description;
    private String thumbnailUrl;

    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;
    @Builder.Default
    private CourseStatus status = CourseStatus.DRAFT;
    @Builder.Default
    private CourseLevel level = CourseLevel.BEGINNER;
    @Builder.Default
    private List<Module> modules = new ArrayList<>();

    private AuditInfo auditInfo;

    public static Course create(String title, String slug, String summary, String description,
                                BigDecimal price, CourseLevel level, String thumbnailUrl,
                                Category category, String instructor){
        Course course = Course.builder()
                .title(title)
                .slug(slug)
                .summary(summary)
                .description(description)
                .price(price != null ? price : BigDecimal.ZERO)
                .level(level != null ? level : CourseLevel.BEGINNER)
                .thumbnailUrl(thumbnailUrl)
                .instructor(instructor)
                .status(CourseStatus.DRAFT)
                .build();
        course.assignCategory(category);
        return course;
    }

    public void updateCourseInfo(String title, String summary, String description,
                                 BigDecimal price, CourseLevel level, String thumbnailUrl){
        this.title = title;
        this.summary = summary;
        this.description = description;
        if(price != null){
            this.price = price;
        }
        if(level != null){
            this.level = level;
        }
        this.thumbnailUrl = thumbnailUrl;
    }

    public void assignCategory(Category category){
        if(category == null){
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        this.category = category;
    }

    // =========================================================
    // (STATE TRANSITIONS)
    // =========================================================

    /**
     * Xuất bản khóa học.
     * có thể đổi từ DRAFT -> SUBMITTED thay vì PUBLISHED trực tiếp.
     */
    public void publish() {

        validatePublishState();

        validatePublishData();

        validatePublishStructure();

        this.status = CourseStatus.PUBLISHED;
    }
    private void validatePublishState(){
        if(this.status == CourseStatus.PUBLISHED){
            throw new BusinessException(
                    ErrorCode.COURSE_INVALID_STATUS,
                    "The course has already been published."
            );
        }
        if(this.status == CourseStatus.ARCHIVED){
            throw new BusinessException(
                    ErrorCode.COURSE_INVALID_STATUS,
                    "An archived course cannot be published."
            );
        }
    }

    private void validatePublishData(){
        if(title == null || title.isBlank()){
            throw new BusinessException(
                    ErrorCode.COURSE_INCOMPLETE, "Course title is required before publishing."
            );
        }
        if(description == null || description.isBlank()){
            throw new BusinessException(
                    ErrorCode.COURSE_INCOMPLETE, "Course description is required before publishing."
            );
        }
        if(thumbnailUrl == null || thumbnailUrl.isBlank()){
            throw new BusinessException(
                    ErrorCode.COURSE_INCOMPLETE, "Course thumbnail is required before publishing."
            );
        }
        if(category == null){
            throw new BusinessException(
                    ErrorCode.COURSE_INCOMPLETE, "Course category is required before publishing."
            );
        }
        if(instructor == null || instructor.isBlank()){
            throw new BusinessException(
                    ErrorCode.COURSE_INCOMPLETE, "Course instructor is required before publishing."
            );
        }
    }

    private void validatePublishStructure(){
        if(modules.isEmpty()){
            throw new BusinessException(
                    ErrorCode.COURSE_HAS_NO_MODULES, "A course must contain at least one module before publishing."
            );
        }
        boolean hasEmptyModule = modules.stream().anyMatch(module -> module.getLessons().isEmpty());
        if(hasEmptyModule){
            throw new BusinessException(ErrorCode.COURSE_HAS_NO_LESSONS);
        }
    }
    /**
     * Lưu trữ khóa học.
     */
    public void archive() {
        if (this.status == CourseStatus.ARCHIVED) {
            throw new BusinessException(ErrorCode.COURSE_ALREADY_ARCHIVED);
        }
        // Lưu ý: Application Service sẽ chịu trách nhiệm check xem có học viên nào đang học không
        // trước khi gọi hàm archive() này.
        this.status = CourseStatus.ARCHIVED;
    }

    /**
     * Tương lai: Khi có quy trình duyệt, chỉ cần mở comment các hàm này
     */
    // public void submitForReview() {
    //     if (this.status != CourseStatus.DRAFT) throw new BusinessException("...");
    //     this.status = CourseStatus.SUBMITTED;
    // }

    // public void reject(String reason) {
    //     if (this.status != CourseStatus.REVIEWING) throw new BusinessException("...");
    //     this.status = CourseStatus.REJECTED;
    //     // Lưu lại reason vào một field nào đó...
    // }
}
