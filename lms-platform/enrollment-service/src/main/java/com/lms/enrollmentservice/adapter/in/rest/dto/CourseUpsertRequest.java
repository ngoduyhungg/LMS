// package: com.lms.courseservice.adapter.in.rest.dto
package com.lms.courseservice.adapter.in.rest.dto;

import com.lms.courseservice.domain.enums.CourseLevel;
import com.lms.courseservice.domain.enums.CourseStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO dùng cho cả hai thao tác POST (tạo) và PUT (cập nhật) của Course.
 *
 * Lưu ý:
 * - `status` không có @NotNull để không bắt buộc khi POST (mặc định DRAFT trong mapper).
 * - Khi PUT, nếu gửi "status": "PUBLISHED" sẽ được ánh xạ qua updateEntityFromRequest.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpsertRequest {

    @NotBlank(message = "Title cannot be blank!")
    @Size(max = 255)
    private String title;

    @Size(max = 500)
    private String summary;

    private String description;

    @DecimalMin("0.00")
    private BigDecimal price;

    @NotNull(message = "Course level cannot be null!")
    private CourseLevel level;

    private Long categoryId;

    private String thumbnailUrl;

    /**
     * Trạng thái của khóa học.
     * Nullable — khi tạo mới (POST), mapper sẽ mặc định là DRAFT.
     * Khi cập nhật (PUT), nếu có giá trị sẽ được ghi vào entity.
     */
    private CourseStatus status;
}
