package com.lms.security.argumentresolver;

import com.lms.security.annotation.AuthenticatedUser;
import com.lms.security.principal.CurrentUser;
import com.lms.security.service.CurrentUserProvider;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final CurrentUserProvider currentUserProvider;

    public CurrentUserArgumentResolver(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class)
                && CurrentUser.class.isAssignableFrom(parameter.getParameterType());
    }
    @Override
    @Nullable
    public CurrentUser resolveArgument(
            @NonNull MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory) {

        return currentUserProvider.getCurrentUser();
    }
}
