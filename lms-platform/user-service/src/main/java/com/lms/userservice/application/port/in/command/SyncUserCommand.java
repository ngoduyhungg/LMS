package com.lms.userservice.application.port.in.command;

public record SyncUserCommand(
        String userId,
        String email,
        String fullName,
        String avatarUrl
) {
}