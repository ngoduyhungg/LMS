package com.lms.courseservice.domain.model;

import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "categories")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //cho JPA Proxy
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Category extends com.lms.shared.entity.AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    @Column(name = "slug", length = 120, nullable = false, unique = true)
    private String slug;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // =========================================================
    // DOMAIN BEHAVIORS (RICH DOMAIN MODEL)
    // =========================================================

    public static Category create(String name, String slug, String description, Category parent){
        Category category = Category.builder()
                .name(name)
                .slug(slug)
                .description(description)
                .build();
        category.assignParent(parent);
        return category;
    }

    public void updateDetails(String newName, String newSlug, String newDescription){
        this.name = newName;
        this.slug = newSlug;
        this.description = newDescription;
    }

    public void assignParent(Category parentCategory){
        if(parentCategory != null && this.getId() != null && this.getId().equals(parentCategory.getId())){
            throw new BusinessException(ErrorCode.CATEGORY_SELF_PARENT, "A category cannot be assigned as its own parent!");
        }
        this.parent = parentCategory;
    }
}
