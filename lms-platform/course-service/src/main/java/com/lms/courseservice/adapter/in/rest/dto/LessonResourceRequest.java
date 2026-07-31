package com.lms.courseservice.adapter.in.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResourceRequest {
    @NotBlank(message = "Resource title cannot be blank!")
    private String title;
    @NotBlank(message = "File URL cannot be blank!")
    private String fileUrl;
    private String fileType;
    @Min(value = 0, message = "File size bytes must be greater than or equal to 0")
    private Long fileSizeBytes;
}
