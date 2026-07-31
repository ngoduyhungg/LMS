package com.lms.shared.validation;

public final class ValidationPatterns {

    private ValidationPatterns() {
    }

    public static final String SLUG =
            "^[a-z0-9]+(?:-[a-z0-9]+)*$";

    public static final String USERNAME =
            "^[a-zA-Z0-9._-]{3,50}$";

    public static final String PHONE =
            "^\\+?[0-9]{9,15}$";

    public static final String PASSWORD =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,100}$";
}
