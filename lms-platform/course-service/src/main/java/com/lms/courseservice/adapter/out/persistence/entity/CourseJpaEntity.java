package com.lms.courseservice.adapter.out.persistence.entity;

import com.lms.courseservice.domain.enums.CourseLevel;
import com.lms.courseservice.domain.enums.CourseStatus;
import com.lms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseJpaEntity extends AuditableEntity {

    @Column(name = "instructor_id", nullable = false)
    private String instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryJpaEntity category;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "slug", length = 280, nullable = false, unique = true)
    private String slug;

    @Column(name = "summary", length = 500)
    private String summary;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CourseStatus status = CourseStatus.DRAFT;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 30)
    private CourseLevel level = CourseLevel.BEGINNER;

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private Set<ModuleJpaEntity> modules = new LinkedHashSet<>();

    public void setModules(Set<ModuleJpaEntity> modules) {
        this.modules.clear();

        if (modules != null) {
            modules.forEach(this::addModule);
        }
    }

    public void addModule(ModuleJpaEntity module) {
        if (module == null) {
            return;
        }

        modules.add(module);
        module.setCourse(this);
    }
}
