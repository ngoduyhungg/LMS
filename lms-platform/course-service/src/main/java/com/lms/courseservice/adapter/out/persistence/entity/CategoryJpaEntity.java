package com.lms.courseservice.adapter.out.persistence.entity;

import com.lms.shared.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryJpaEntity extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CategoryJpaEntity parent;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "slug", length = 120, nullable = false, unique = true)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
