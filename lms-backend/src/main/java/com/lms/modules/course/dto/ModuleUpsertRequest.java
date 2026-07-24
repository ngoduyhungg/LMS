package com.lms.modules.course.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleUpsertRequest {
    @NotBlank(message = "Title cannot be blank!")
    private String title;
    @Min(0)
    private Integer sortOrder;
}
