FROM quay.io/keycloak/keycloak:26.2.0 AS builder

# =============================================================================
# Keycloak 26.2.0 — Custom Optimized Image
#
# PURPOSE: Bake build-time options into the image at Docker build time.
#
# ABOUT BUILD-TIME vs RUNTIME OPTIONS:
#   Keycloak (Quarkus) distinguishes two types of configuration:
#   - Build-time options: must be set during `kc.sh build`; they alter how
#     Quarkus bytecode is generated and cannot be changed at runtime.
#   - Runtime options: passed as env vars or flags when the server starts.
#
#   KC_TRACING_ENABLED=true is a BUILD-TIME option.
#   Without running `kc.sh build` with it, passing KC_TRACING_ENABLED=true
#   to `start` causes: "build time option UNAVAILABLE: tracing-enabled".
#
#   NOTE: `start-dev` does auto-rebuild when it detects changed build-time
#   options, which is why KC_TRACING_ENABLED sometimes works without a custom
#   image. However, we use a custom image to:
#     1. Make startup faster (no rebuild on every container start)
#     2. Make behavior deterministic across environments
#     3. Fail fast at `docker compose build` rather than at runtime
#
# ABOUT KC_FEATURES=opentelemetry:
#   In Keycloak 26.x, the `opentelemetry` feature is ENABLED BY DEFAULT.
#   We still set it explicitly here for clarity and forward-compatibility.
#   It does NOT cause any harm to set it even if already enabled.
#
# =============================================================================

WORKDIR /opt/keycloak

# Build-time options — baked into Quarkus augmentation artifacts
ENV KC_DB=postgres
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true
ENV KC_FEATURES=opentelemetry
ENV KC_TRACING_ENABLED=true

RUN /opt/keycloak/bin/kc.sh build

# =============================================================================
# Runtime image — clean copy of pre-built artifacts
# Multi-stage ensures no build tooling remains in the final image.
# =============================================================================
FROM quay.io/keycloak/keycloak:26.2.0

COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
