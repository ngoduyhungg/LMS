package com.lms.courseservice.adapter.out.persistence.entity;

import com.lms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleJpaEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseJpaEntity course;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @OneToMany(
            mappedBy = "module",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private Set<LessonJpaEntity> lessons = new LinkedHashSet<>();

    public void setLessons(Set<LessonJpaEntity> lessons) {
        this.lessons.clear();

        if (lessons != null) {
            lessons.forEach(this::addLesson);
        }
    }

    public void addLesson(LessonJpaEntity lesson) {
        if (lesson == null) {
            return;
        }

        lessons.add(lesson);
        lesson.setModule(this);
    }
}
