package com.lms.userservice.adapter.out.persistence.mapper;

import com.lms.userservice.adapter.out.persistence.entity.UserJpaEntity;
import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface UserPersistenceMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "mapToUserId")
    // Map từ field phẳng của Entity vào đối tượng auditInfo của Domain[cite: 18]
    @Mapping(target = "auditInfo.createdAt", source = "createdAt")
    @Mapping(target = "auditInfo.updatedAt", source = "updatedAt")
    User toDomain(UserJpaEntity entity);

    @Mapping(target = "id", source = "id.value")
    // Map ngược từ auditInfo của Domain ra field phẳng của Entity[cite: 18]
    @Mapping(target = "createdAt", source = "auditInfo.createdAt")
    @Mapping(target = "updatedAt", source = "auditInfo.updatedAt")
    UserJpaEntity toEntity(User domain);

    @Named("mapToUserId")
    default UserId mapToUserId(String id) {
        return id != null ? new UserId(id) : null;
    }
}