package com.lms.userservice.adapter.in.rest.mapper;

import com.lms.security.principal.CurrentUser;
import com.lms.userservice.adapter.in.rest.dto.InternalUserProfileResponse;
import com.lms.userservice.adapter.in.rest.dto.UserProfileResponse;
import com.lms.userservice.application.port.in.command.SyncUserCommand;
import com.lms.userservice.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRestMapper {

    public InternalUserProfileResponse toInternalProfileResponse(User user) {
        if (user == null) return null;
        return InternalUserProfileResponse.builder()
                .userId(user.getId().value())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    public SyncUserCommand toSyncCommand(CurrentUser currentUser) {
        if (currentUser == null) return null;
        return new SyncUserCommand(
                currentUser.id(),
                currentUser.email(),
                currentUser.fullName(),
                null // Avatar chưa có từ token cơ bản, default null
        );
    }

    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) return null;
        return UserProfileResponse.builder()
                .userId(user.getId().value())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus().name())
                .build();
    }
    public List<InternalUserProfileResponse> toInternalProfileResponses(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .map(this::toInternalProfileResponse)
                .toList();
    }
}