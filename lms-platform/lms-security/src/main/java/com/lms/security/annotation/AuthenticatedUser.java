package com.lms.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for resolving the currently authenticated user
 * from a Keycloak-issued JWT token in Spring MVC controller method parameters.
 *
 * <h3>Usage</h3>
 * Annotate any controller method parameter of type {@link com.lms.security.principal.CurrentUser}:
 * <pre>{@code
 * @GetMapping("/me")
 * public ResponseEntity<UserProfileResponse> getMyProfile(
 *         @AuthenticatedUser CurrentUser currentUser) {
 *     // currentUser is fully populated from the JWT — no manual extraction needed
 * }
 * }</pre>
 *
 * <h3>How it works</h3>
 * Spring MVC calls {@link com.lms.security.argumentresolver.CurrentUserArgumentResolver}
 * for every controller parameter. The resolver checks whether the parameter carries
 * this annotation; if it does, it delegates to
 * {@link com.lms.security.service.CurrentUserProvider} to extract the
 * {@code CurrentUser} from the active {@code SecurityContext}.
 *
 * <h3>Contract</h3>
 * <ul>
 *   <li>The resolved parameter type MUST be {@code CurrentUser}.</li>
 *   <li>If no valid JWT is present in the security context, the resolver
 *       returns {@code null}. Protect the endpoint with
 *       {@code @PreAuthorize("isAuthenticated()")} or Spring Security's
 *       {@code .authenticated()} DSL to guarantee a non-null value.</li>
 *   <li>This annotation is only meaningful on method parameters — applying it
 *       elsewhere has no effect.</li>
 * </ul>
 *
 * @see com.lms.security.argumentresolver.CurrentUserArgumentResolver
 * @see com.lms.security.principal.CurrentUser
 * @see com.lms.security.service.CurrentUserProvider
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthenticatedUser {
}