package com.lms.modules.course.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.common.entity.AuditableEntity;
import com.lms.modules.course.enums.CourseLevel;
import com.lms.modules.course.enums.CourseStatus;
import com.lms.modules.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Table(name = "courses")
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
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
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC")
    @JsonIgnore
    @Builder.Default
    private List<Module> modules = new ArrayList<>();
}
