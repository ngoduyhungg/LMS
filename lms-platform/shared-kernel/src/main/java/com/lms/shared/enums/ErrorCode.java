package com.lms.shared.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // =========================================================
    // COMMON
    // =========================================================

    INTERNAL_SERVER_ERROR(
            "INTERNAL_SERVER_ERROR",
            "Internal server error."
    ),

    VALIDATION_ERROR(
            "VALIDATION_ERROR",
            "Validation failed."
    ),

    ILLEGAL_ARGUMENT(
            "ILLEGAL_ARGUMENT",
            "Illegal argument."
    ),

    ACCESS_DENIED(
            "ACCESS_DENIED",
            "Access denied."
    ),

    UNAUTHORIZED(
            "UNAUTHORIZED",
            "Authentication required."
    ),

    // =========================================================
    // USER
    // =========================================================

    USER_NOT_FOUND(
            "USER_NOT_FOUND",
            "User not found with id: %s"
    ),

    USER_ALREADY_EXISTS(
            "USER_ALREADY_EXISTS",
            "User already exists."
    ),

    // =========================================================
    // COURSE
    // =========================================================

    COURSE_NOT_FOUND(
            "COURSE_NOT_FOUND",
            "Course not found with id: %s"
    ),

    COURSE_ALREADY_EXISTS(
            "COURSE_ALREADY_EXISTS",
            "Course already exists."
    ),

    COURSE_INVALID_STATUS(
            "COURSE_INVALID_STATUS",
            "Course is in an invalid state."
    ),

    COURSE_INCOMPLETE(
            "COURSE_INCOMPLETE",
            "Course information is incomplete."
    ),

    COURSE_HAS_NO_MODULES(
            "COURSE_HAS_NO_MODULES",
            "Course must contain at least one module."
    ),

    COURSE_HAS_NO_LESSONS(
            "COURSE_HAS_NO_LESSONS",
            "Each module must contain at least one lesson."
    ),

    COURSE_ALREADY_ARCHIVED(
            "COURSE_ALREADY_ARCHIVED",
            "Course has already been archived."
    ),

    // =========================================================
    // CATEGORY
    // =========================================================

    CATEGORY_NOT_FOUND(
            "CATEGORY_NOT_FOUND",
            "Category not found with id: %s"
    ),

    CATEGORY_ALREADY_EXISTS(
            "CATEGORY_ALREADY_EXISTS",
            "Category already exists."
    ),

    CATEGORY_SELF_PARENT(
            "CATEGORY_SELF_PARENT",
            "A category cannot be assigned as its own parent."
    ),

    CATEGORY_HAS_CHILDREN(
            "CATEGORY_HAS_CHILDREN",
            "Category cannot be deleted because it has child categories."
    ),

    CATEGORY_IN_USE(
            "CATEGORY_IN_USE",
            "Category is currently in use."
    ),

    // =========================================================
    // MODULE
    // =========================================================

    MODULE_NOT_FOUND(
            "MODULE_NOT_FOUND",
            "Module not found with id: %s"
    ),

    MODULE_ALREADY_EXISTS(
            "MODULE_ALREADY_EXISTS",
            "Module already exists."
    ),

    // =========================================================
    // LESSON
    // =========================================================

    LESSON_NOT_FOUND(
            "LESSON_NOT_FOUND",
            "Lesson not found with id: %s"
    ),

    LESSON_ALREADY_COMPLETED(
            "LESSON_ALREADY_COMPLETED",
            "Lesson has already been completed."
    ),

    // =========================================================
    // AUTH
    // =========================================================

    INVALID_TOKEN(
            "INVALID_TOKEN",
            "Invalid access token."
    ),

    TOKEN_EXPIRED(
            "TOKEN_EXPIRED",
            "Access token has expired."
    );

    private final String code;

    private final String message;
}