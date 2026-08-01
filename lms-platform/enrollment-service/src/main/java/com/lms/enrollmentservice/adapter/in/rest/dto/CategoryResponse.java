package com.lms.courseservice.adapter.in.rest.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private Long parentCategoryId;
}
