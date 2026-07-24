package com.lms.modules.course.entity;

import com.lms.common.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "categories")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    @Column(name = "slug", length = 120, nullable = false, unique = true)
    private String slug;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
