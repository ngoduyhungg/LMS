package com.lms.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(

        boolean success,

        String code,

        String message,

        T data,

        Map<String, String> errors,

        LocalDateTime timestamp

) {

    // =========================================================
    // SUCCESS
    // =========================================================

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return new ApiResponse<>(
                true,
                null,
                message,
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

    // =========================================================
    // ERROR
    // =========================================================

    public static ApiResponse<Void> error(
            String code,
            String message
    ) {
        return new ApiResponse<>(
                false,
                code,
                message,
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
                "VALIDATION_ERROR",
                message,
                null,
                errors,
                LocalDateTime.now()
        );
    }

}