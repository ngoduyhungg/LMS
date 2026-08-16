package com.lms.userservice.adapter.in.rest;

import com.lms.security.annotation.AuthenticatedUser;
import com.lms.security.principal.CurrentUser;
import com.lms.userservice.adapter.in.rest.dto.UserProfileResponse;
import com.lms.userservice.adapter.in.rest.mapper.UserRestMapper;
import com.lms.userservice.application.port.in.SyncUserProfileUseCase;
import com.lms.userservice.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final SyncUserProfileUseCase syncUserProfileUseCase;
    private final UserRestMapper mapper;

    @PostMapping("/sync")
    @PreAuthorize("isAuthenticated()")
    public UserProfileResponse syncUser(@AuthenticatedUser CurrentUser currentUser) {
        var command = mapper.toSyncCommand(currentUser);
        User syncedUser = syncUserProfileUseCase.syncProfile(command);
        return mapper.toProfileResponse(syncedUser);
    }
}