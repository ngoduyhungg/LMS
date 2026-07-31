package com.lms.security.principal;

import java.util.Set;

public record CurrentUser(

        String id,

        String username,

        String email,

        Set<String> roles

) {

    public boolean isAdmin() {
        return roles.contains("ROLE_ADMIN");
    }

    public boolean isInstructor() {
        return roles.contains("ROLE_INSTRUCTOR");
    }

    public boolean isStudent() {
        return roles.contains("ROLE_STUDENT");
    }

}