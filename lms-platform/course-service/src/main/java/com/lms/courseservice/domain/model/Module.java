package com.lms.courseservice.domain.model;

import com.lms.courseservice.domain.shared.AuditInfo;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {
    private Long id;
    private Course course;
    private String title;

    @Builder.Default
    private Integer sortOrder = 0;

    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>();

    private AuditInfo auditInfo;

    public static Module create(Course course, String title, Integer sortOrder){
        return Module.builder()
                .course(course)
                .title(title)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .lessons(new ArrayList<>())
                .build();
    }

    public void updateDetails(String title, Integer sortOrder){
        this.title = title;
        if(sortOrder != null){
            this.sortOrder = sortOrder;
        }
    }
}
