package com.lms.courseservice.adapter.in.rest.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleResponse {
    private Long id;
    private String title;
    private Integer sortOrder;
}
