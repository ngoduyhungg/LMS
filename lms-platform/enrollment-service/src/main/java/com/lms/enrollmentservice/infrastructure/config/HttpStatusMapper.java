package com.lms.enrollmentservice.infrastructure.config;

import com.lms.shared.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import java.util.EnumMap;
import java.util.Map;

public final class HttpStatusMapper {

    private HttpStatusMapper() {
    }

    private static final Map<ErrorCode, HttpStatus> STATUS_MAP =
            new EnumMap<>(ErrorCode.class);

    static {

        // ===== COMMON =====

        STATUS_MAP.put(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        STATUS_MAP.put(ErrorCode.VALIDATION_ERROR, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(ErrorCode.ILLEGAL_ARGUMENT, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(ErrorCode.ACCESS_DENIED, HttpStatus.FORBIDDEN);
        STATUS_MAP.put(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);

        // ===== USER =====

        STATUS_MAP.put(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        STATUS_MAP.put(ErrorCode.USER_ALREADY_EXISTS, HttpStatus.CONFLICT);

        // ===== COURSE =====

        STATUS_MAP.put(ErrorCode.COURSE_NOT_FOUND, HttpStatus.NOT_FOUND);
        STATUS_MAP.put(ErrorCode.COURSE_ALREADY_EXISTS, HttpStatus.CONFLICT);
        STATUS_MAP.put(ErrorCode.COURSE_INVALID_STATUS, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(ErrorCode.COURSE_INCOMPLETE, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(ErrorCode.COURSE_HAS_NO_MODULES, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(ErrorCode.COURSE_HAS_NO_LESSONS, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(ErrorCode.COURSE_ALREADY_ARCHIVED, HttpStatus.CONFLICT);

        // ===== CATEGORY =====

        STATUS_MAP.put(ErrorCode.CATEGORY_NOT_FOUND, HttpStatus.NOT_FOUND);
        STATUS_MAP.put(ErrorCode.CATEGORY_ALREADY_EXISTS, HttpStatus.CONFLICT);
        STATUS_MAP.put(ErrorCode.CATEGORY_SELF_PARENT, HttpStatus.BAD_REQUEST);
        STATUS_MAP.put(ErrorCode.CATEGORY_HAS_CHILDREN, HttpStatus.CONFLICT);
        STATUS_MAP.put(ErrorCode.CATEGORY_IN_USE, HttpStatus.CONFLICT);

        // ===== MODULE =====

        STATUS_MAP.put(ErrorCode.MODULE_NOT_FOUND, HttpStatus.NOT_FOUND);
        STATUS_MAP.put(ErrorCode.MODULE_ALREADY_EXISTS, HttpStatus.CONFLICT);

        // ===== LESSON =====

        STATUS_MAP.put(ErrorCode.LESSON_NOT_FOUND, HttpStatus.NOT_FOUND);
        STATUS_MAP.put(ErrorCode.LESSON_ALREADY_COMPLETED, HttpStatus.CONFLICT);

        // ===== AUTH =====

        STATUS_MAP.put(ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        STATUS_MAP.put(ErrorCode.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);

        // ===== CERTIFICATE =====
        STATUS_MAP.put(ErrorCode.CERTIFICATE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public static HttpStatus getStatus(ErrorCode errorCode) {
        return STATUS_MAP.getOrDefault(
                errorCode,
                HttpStatus.BAD_REQUEST
        );
    }

}