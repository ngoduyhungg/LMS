package com.lms.common.response;

import com.lms.common.enums.ErrorCode;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiResponse<T>(
        boolean success,
        String message,
        ErrorCode errorCode,
        T data,
        Map<String, String> errors,
        LocalDateTime timestamp
) {

    // ===== SUCCESS =====

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(
                true,
                message,
                null,
                data,
                null,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Success", data);
    }

    public static ApiResponse<Void> successMessage(String message) {
        return success(message, null);
    }

    // ===== ERROR =====

    public static ApiResponse<Void> error(ErrorCode errorCode, String message) {
        return new ApiResponse<>(
                false,
                message,
                errorCode,
                null,
                null,
                LocalDateTime.now()
        );
    }

    public static ApiResponse<Void> validationError(
            String message,
            Map<String, String> errors
    ) {
        return new ApiResponse<>(
                false,
                message,
                ErrorCode.VALIDATION_ERROR,
                null,
                errors,
                LocalDateTime.now()
        );
    }
}
