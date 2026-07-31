package com.lms.security.util;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

/**
 * Tiện ích hỗ trợ xử lý Security, phân quyền và phòng chống IDOR.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Private constructor to hide the implicit public one
    }

    /**
     * Trích xuất JWT từ SecurityContext và trả về subject (UUID của user hiện tại).
     */
    public static String getCurrentUserId() {
        JwtAuthenticationToken auth =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return auth.getToken().getSubject();
    }

    /**
     * Kiểm tra xem user hiện tại có quyền ROLE_ADMIN không.
     * Nếu có ADMIN, bypass kiểm tra ownership.
     */
    public static boolean isAdmin() {
        JwtAuthenticationToken auth =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        return authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Kiểm tra quyền sở hữu tài nguyên.
     * Logic:
     * - Nếu user là ADMIN → bỏ qua, không cần kiểm tra.
     * - Nếu user là INSTRUCTOR → UUID phải khớp với instructorId.
     * - Không khớp → ném AccessDeniedException (HTTP 403 Forbidden).
     *
     * @param resourceInstructorId UUID của giảng viên sở hữu tài nguyên (từ entity).
     */
    public static void checkOwnership(String resourceInstructorId) {
        if (isAdmin()) {
            return; // Admin được phép thao tác mọi tài nguyên
        }
        String currentUserId = getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(resourceInstructorId)) {
            throw new AccessDeniedException(
                    "Access Denied: You do not have permission to modify this resource. " +
                            "Only the resource owner or an ADMIN can perform this action.");
        }
    }
}