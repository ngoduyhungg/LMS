package com.lms.courseservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "modules")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module extends com.lms.shared.entity.AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @Column(name = "title", length = 255, nullable = false)
    private String title;
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    @JsonIgnore
    @Builder.Default
    private List<Lesson> lessons = new ArrayList<>();

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
