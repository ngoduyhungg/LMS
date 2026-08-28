package com.lms.userservice.application.service;

import com.lms.shared.enums.ErrorCode;
import com.lms.shared.exception.BusinessException;
import com.lms.userservice.application.port.in.GetInternalUserProfileUseCase;
import com.lms.userservice.application.port.in.SyncUserProfileUseCase;
import com.lms.userservice.application.port.in.command.SyncUserCommand;
import com.lms.userservice.application.port.out.UserRepositoryPort;
import com.lms.userservice.domain.enums.UserStatus;
import com.lms.userservice.domain.model.User;
import com.lms.userservice.domain.model.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserApplicationService implements GetInternalUserProfileUseCase, SyncUserProfileUseCase {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public User getInternalProfile(UserId userId) {
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.USER_NOT_FOUND,
                        userId.value()
                ));
    }

    @Override
    @Transactional
    public User syncProfile(SyncUserCommand command) {
        // Validate mandatory fields
        if (command.userId() == null || command.userId().isBlank()) {
            throw new BusinessException(ErrorCode.ILLEGAL_ARGUMENT, "User ID is required");
        }
        if (command.email() == null || command.email().isBlank()) {
            throw new BusinessException(ErrorCode.ILLEGAL_ARGUMENT, "Email is required");
        }
        if (command.fullName() == null || command.fullName().isBlank()) {
            throw new BusinessException(ErrorCode.ILLEGAL_ARGUMENT, "Full name is required");
        }

        UserId userId = new UserId(command.userId());

        // Case 1: User đã tồn tại
        return userRepositoryPort.findById(userId)
                .orElseGet(() -> {
                    // Case 2: User chưa tồn tại -> Create
                    User newUser = User.builder()
                            .id(userId)
                            .email(command.email())
                            .fullName(command.fullName())
                            .avatarUrl(command.avatarUrl())
                            .status(UserStatus.ACTIVE) // Default ACTIVE
                            .build();

                    try {
                        return userRepositoryPort.save(newUser);
                    } catch (BusinessException ex) {
                        // Xử lý Race Condition: Nếu lỗi là USER_ALREADY_EXISTS, thử tìm lại profile
                        if (ErrorCode.USER_ALREADY_EXISTS.equals(ex.getErrorCode())) {
                            return userRepositoryPort.findById(userId)
                                    .orElseThrow(() -> ex); // Nếu KHÔNG tìm thấy, propagate lỗi nguyên bản
                        }
                        throw ex; // Nếu là lỗi business khác, propagate
                    }
                });
    }
    @Override
    @Transactional(readOnly = true)
    public List<User> getBatchInternalProfiles(Set<UserId> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }
        return userRepositoryPort.findByIds(userIds);
    }
}