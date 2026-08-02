package com.lms.courseservice.adapter.in.rest.mapper;

import com.lms.courseservice.adapter.in.rest.dto.CategoryResponse;
import com.lms.courseservice.adapter.in.rest.dto.CategoryUpsertRequest;
import com.lms.courseservice.application.port.in.command.CategoryCommand;
import com.lms.courseservice.domain.model.Category;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface CategoryRestMapper {
    CategoryCommand toCommand(CategoryUpsertRequest request);
    @Mapping(source = "parent.id", target = "parentCategoryId")
    CategoryResponse toResponse(Category category);

    List<CategoryResponse> toResponseList(List<Category> categories);
}
