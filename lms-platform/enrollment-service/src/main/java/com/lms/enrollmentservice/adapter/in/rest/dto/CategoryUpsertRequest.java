package com.lms.courseservice.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryUpsertRequest {
    @NotBlank(message = "Name must not be blank!")
    @Size(max = 100, message = "Name must not exceed 100 characters!")
    private String name;
    private String description;
    private Long parentCategoryId;
}
