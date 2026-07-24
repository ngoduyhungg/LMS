package com.lms.modules.auth.entity;
import com.lms.common.entity.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Table(name = "permissions")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends AuditableEntity{
    @Column(name = "name", length = 100,nullable = false, unique = true)
    private String name;
    @Column(name = "module", length = 50, nullable = false)
    private String module;
    @Column(name = "description", length = 255)
    private String description;
}
