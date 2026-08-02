package com.lms.courseservice.domain.model;

import com.lms.courseservice.domain.shared.AuditInfo;
import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    private Long id;
    private Category parent;
    private String name;
    private String slug;
    private String description;

    private AuditInfo auditInfo;

    // =========================================================
    // DOMAIN BEHAVIORS
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
