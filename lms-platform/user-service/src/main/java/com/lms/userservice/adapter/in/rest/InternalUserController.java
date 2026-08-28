package com.lms.userservice.adapter.in.rest;

import com.lms.userservice.adapter.in.rest.dto.BatchInternalUserProfileRequest;
import com.lms.userservice.adapter.in.rest.dto.InternalUserProfileResponse;
import com.lms.userservice.adapter.in.rest.mapper.UserRestMapper;
import com.lms.userservice.application.port.in.GetInternalUserProfileUseCase;
import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @PostMapping("/profiles/batch")
    @PreAuthorize("isAuthenticated()")
    public List<InternalUserProfileResponse> getBatchProfiles(
            @Valid @RequestBody BatchInternalUserProfileRequest request) {

        Set<UserId> userIds = request.getUserIds().stream()
                .map(UserId::new)
                .collect(Collectors.toSet());

        List<User> users = getUseCase.getBatchInternalProfiles(userIds);

        return mapper.toInternalProfileResponses(users);
    }
}