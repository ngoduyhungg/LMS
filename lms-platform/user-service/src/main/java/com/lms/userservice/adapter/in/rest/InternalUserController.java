package com.lms.userservice.adapter.in.rest;

import com.lms.userservice.adapter.in.rest.dto.InternalUserProfileResponse;
import com.lms.userservice.adapter.in.rest.mapper.UserRestMapper;
import com.lms.userservice.application.port.in.GetInternalUserProfileUseCase;
import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final GetInternalUserProfileUseCase getUseCase;
    private final UserRestMapper mapper;

    /**
     * Phục vụ các service khác (như enrollment-service).
     * Bắt buộc có token nội bộ / token hợp lệ từ Keycloak.
     * Phụ thuộc hoàn toàn vào cấu hình của lms-security.
     */
    @GetMapping("/{userId}/profile")
    @PreAuthorize("isAuthenticated()")
    public InternalUserProfileResponse getInternalProfile(@PathVariable String userId) {
        User user = getUseCase.getInternalProfile(new UserId(userId));
        return mapper.toInternalProfileResponse(user);
    }
}