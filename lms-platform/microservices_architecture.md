# 🏗️ LMS Platform — Microservices Architecture Blueprint
> Stack: Spring Boot 4.1.0 · Spring Cloud 2025.x · Keycloak 26 · OpenTelemetry · PostgreSQL 16 · Docker

---

## PHẦN 1: KIẾN TRÚC HỆ THỐNG

### 1.1 — Sơ đồ ASCII Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          LMS PLATFORM — MICROSERVICES                          │
└─────────────────────────────────────────────────────────────────────────────────┘

  Browser / React 18 Frontend (localhost:3000)
       │
       │  HTTPS + JWT Bearer Token
       ▼
┌──────────────────────────────────────────┐
│           API GATEWAY (port 8080)         │
│      Spring Cloud Gateway 2025.x          │
│  ┌─────────────────────────────────────┐  │
│  │  Route: /api/courses/**             │  │
│  │  Route: /api/enrollments/**         │  │
│  │  Filter: TokenRelay + CORS          │  │
│  └─────────────────────────────────────┘  │
└───────┬──────────────────────┬────────────┘
        │                      │
        │ forward JWT           │ forward JWT
        ▼                      ▼
┌───────────────┐     ┌────────────────────┐     ┌───────────────────┐
│ COURSE SERVICE│     │ ENROLLMENT SERVICE  │     │  (future services)│
│  (port 8081)  │     │    (port 8082)       │     │  quiz / notif...  │
│               │     │                     │     │                   │
│ Spring Boot 4 │     │  Spring Boot 4       │     │  Spring Boot 4    │
│ + JPA/Flyway  │     │  + JPA/Flyway        │     │                   │
└──────┬────────┘     └─────────┬───────────┘     └───────────────────┘
       │                        │
       │ validate JWT (JWK)     │ validate JWT (JWK)
       ▼                        ▼
┌──────────────────────────────────────────┐
│         KEYCLOAK IAM (port 8180)          │
│  quay.io/keycloak/keycloak:26.0.7         │
│  Realm: lms-realm                        │
│  Roles: ADMIN / INSTRUCTOR / STUDENT     │
│  JWK URI: /realms/lms-realm/protocol/    │
│           openid-connect/certs           │
└──────────────┬───────────────────────────┘
               │ persist users/sessions
               ▼
┌──────────────────────────────────────────┐
│         POSTGRESQL 16 (port 5433)         │
│  ┌────────────┐  ┌───────────────────┐   │
│  │keycloak_db │  │   course_db       │   │
│  │            │  │  (Flyway managed) │   │
│  └────────────┘  └───────────────────┘   │
│  ┌────────────┐  ┌───────────────────┐   │
│  │enrollment_db│  │  notification_db  │   │
│  └────────────┘  └───────────────────┘   │
└──────────────────────────────────────────┘

═══════════════ OBSERVABILITY STACK ═══════════════

  Course Service ──OTel SDK──► JAEGER (port 4317/4318)
  API Gateway    ──OTel SDK──► Jaeger UI (port 16686)
  All Services   ──Actuator──► PROMETHEUS (port 9090)
  Prometheus     ──datasource──► GRAFANA (port 3001)
```

### 1.2 — Request Flow (Happy Path)

```
1. React POST /api/auth/token  ──► Keycloak (Direct Grant)
                                    Returns: { access_token, refresh_token }

2. React GET /api/courses      ──► API Gateway
   Authorization: Bearer <JWT>      │
                                    ├─► Route to Course Service :8081
                                    │
                                    └─► Course Service validates JWT via
                                        Keycloak JWK Set URI (no DB call!)
                                        Extracts realm_access.roles → ROLE_INSTRUCTOR

3. Course Service responds      ──► API Gateway ──► React
   (with OTel trace propagation via W3C TraceContext headers)
```

---

## PHẦN 2: FOLDER TREE — Multi-Module Maven

```
lms-platform/                               ← Root project (Git root)
│
├── pom.xml                                 ← Parent POM (manages versions)
├── docker-compose.yml                      ← All infrastructure
├── docker/
│   ├── postgres/
│   │   └── init-multiple-dbs.sh           ← Auto-create keycloak_db, course_db, ...
│   ├── keycloak/
│   │   └── realms/
│   │       └── lms-realm.json             ← Auto-import realm on Keycloak startup
│   └── prometheus/
│       └── prometheus.yml
│
├── shared-kernel/                          ← [MODULE] Shared DTOs & utilities
│   ├── pom.xml
│   └── src/main/java/com/lms/shared/
│       ├── dto/
│       │   └── ApiResponse.java
│       ├── exception/
│       │   ├── ResourceNotFoundException.java
│       │   └── BusinessException.java
│       └── util/
│           └── SlugUtils.java
│
├── api-gateway/                            ← [MODULE] Spring Cloud Gateway
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/lms/gateway/
│       │   ├── ApiGatewayApplication.java
│       │   └── config/
│       │       ├── GatewaySecurityConfig.java
│       │       └── CorsConfig.java
│       └── resources/
│           └── application.yml
│
└── course-service/                         ← [MODULE] Course Microservice
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/com/lms/courseservice/
        │   │
        │   │   ── TẦNG DOMAIN (Business Logic, NO framework deps)
        │   │   ├── domain/
        │   │   │   ├── model/              ← JPA Entities (pragmatic: keep JPA here)
        │   │   │   │   ├── Course.java
        │   │   │   │   ├── Module.java
        │   │   │   │   ├── Lesson.java
        │   │   │   │   ├── LessonResource.java
        │   │   │   │   └── Category.java
        │   │   │   ├── enums/
        │   │   │   │   ├── CourseStatus.java
        │   │   │   │   ├── CourseLevel.java
        │   │   │   │   └── LessonType.java
        │   │   │   └── exception/
        │   │   │       ├── CourseNotFoundException.java
        │   │   │       └── DuplicateSlugException.java
        │   │   │
        │   │   ── TẦNG APPLICATION (Use Cases / Ports)
        │   │   ├── application/
        │   │   │   ├── port/
        │   │   │   │   ├── in/             ← Input Ports (Use Case interfaces)
        │   │   │   │   │   ├── GetCourseUseCase.java
        │   │   │   │   │   ├── CreateCourseUseCase.java
        │   │   │   │   │   ├── ManageModuleUseCase.java
        │   │   │   │   │   └── ManageLessonUseCase.java
        │   │   │   │   └── out/            ← Output Ports (Repository interfaces)
        │   │   │   │       ├── CourseRepositoryPort.java
        │   │   │   │       ├── ModuleRepositoryPort.java
        │   │   │   │       └── LessonRepositoryPort.java
        │   │   │   └── service/            ← Application Services (implement ports)
        │   │   │       └── CourseApplicationService.java
        │   │   │
        │   │   ── TẦNG ADAPTER (Thế giới bên ngoài giao tiếp với Application)
        │   │   ├── adapter/
        │   │   │   ├── in/
        │   │   │   │   └── rest/           ← REST Controllers (Inbound Adapter)
        │   │   │   │       ├── CourseController.java
        │   │   │   │       └── dto/        ← HTTP Request/Response DTOs
        │   │   │   │           ├── CourseResponse.java
        │   │   │   │           ├── CourseUpsertRequest.java
        │   │   │   │           ├── ModuleResponse.java
        │   │   │   │           ├── ModuleUpsertRequest.java
        │   │   │   │           ├── LessonResponse.java
        │   │   │   │           ├── LessonUpsertRequest.java
        │   │   │   │           └── CourseCurriculumResponse.java
        │   │   │   └── out/
        │   │   │       └── persistence/    ← JPA Adapters (Outbound Adapter)
        │   │   │           ├── CourseJpaRepository.java    (Spring Data JPA)
        │   │   │           ├── ModuleJpaRepository.java
        │   │   │           ├── LessonJpaRepository.java
        │   │   │           ├── CategoryJpaRepository.java
        │   │   │           └── mapper/                    ← MapStruct Mappers
        │   │   │               ├── CourseMapper.java
        │   │   │               ├── ModuleMapper.java
        │   │   │               └── LessonMapper.java
        │   │   │
        │   │   ── TẦNG INFRASTRUCTURE (Framework & Config)
        │   │   └── infrastructure/
        │   │       ├── config/
        │   │       │   ├── SecurityConfig.java             ← Keycloak JWT config
        │   │       │   ├── KeycloakJwtConverter.java       ← realm_access.roles extractor
        │   │       │   └── OpenTelemetryConfig.java
        │   │       └── CourseServiceApplication.java
        │   │
        │   └── resources/
        │       ├── application.yml
        │       └── db/migration/
        │           ├── V1__init_course_schema.sql
        │           └── V2__insert_course_seed_data.sql
        └── test/
```

---

## PHẦN 3: PARENT POM.XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.0</version>
        <relativePath/>
    </parent>

    <groupId>com.lms</groupId>
    <artifactId>lms-platform</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>LMS Platform — Parent</name>

    <modules>
        <module>shared-kernel</module>
        <module>api-gateway</module>
        <module>course-service</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2025.0.0</spring-cloud.version>
        <mapstruct.version>1.6.3</mapstruct.version>
        <lombok.version>1.18.36</lombok.version>
        <opentelemetry.version>2.9.0</opentelemetry.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Cloud BOM -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- Shared Kernel -->
            <dependency>
                <groupId>com.lms</groupId>
                <artifactId>shared-kernel</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

---

## PHẦN 3: COURSE SERVICE — `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.lms</groupId>
        <artifactId>lms-platform</artifactId>
        <version>1.0.0-SNAPSHOT</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>course-service</artifactId>
    <name>LMS — Course Microservice</name>

    <dependencies>
        <!-- ========== SPRING BOOT CORE ========== -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webmvc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- ========== SECURITY + KEYCLOAK JWT ========== -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
            <!-- Uses NimbusJwtDecoder — validates against Keycloak JWK Set URI -->
        </dependency>

        <!-- ========== DATABASE ========== -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-flyway</artifactId>
        </dependency>
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-database-postgresql</artifactId>
        </dependency>

        <!-- ========== OBSERVABILITY (OTel + Micrometer) ========== -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <!-- Micrometer Tracing Bridge — connects Micrometer to OTel SDK -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-tracing-bridge-otel</artifactId>
        </dependency>
        <!-- OTel Exporter → sends traces to Jaeger via OTLP -->
        <dependency>
            <groupId>io.opentelemetry</groupId>
            <artifactId>opentelemetry-exporter-otlp</artifactId>
        </dependency>
        <!-- Prometheus metrics exporter -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
            <scope>runtime</scope>
        </dependency>
        <!-- Propagate trace headers to downstream HTTP calls -->
        <dependency>
            <groupId>io.opentelemetry.instrumentation</groupId>
            <artifactId>opentelemetry-spring-boot-starter</artifactId>
            <version>2.9.0</version>
        </dependency>

        <!-- ========== DEVELOPER TOOLS ========== -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>1.6.3</version>
        </dependency>

        <!-- ========== SHARED ========== -->
        <dependency>
            <groupId>com.lms</groupId>
            <artifactId>shared-kernel</artifactId>
        </dependency>

        <!-- ========== TEST ========== -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webmvc-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>1.6.3</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## PHẦN 3: COURSE SERVICE — `application.yml`

```yaml
server:
  port: 8081

spring:
  application:
    name: course-service   # Used as OTel service.name

  # ── Database: course_db (isolated per service) ─────────────────
  datasource:
    url: jdbc:postgresql://localhost:5433/course_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      idle-timeout: 30000

  jpa:
    open-in-view: false
    show-sql: true
    hibernate:
      ddl-auto: validate
    properties:
      "[hibernate.format_sql]": true
      "[hibernate.use_sql_comments]": true

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

  # ── Security: Keycloak as OAuth2 Resource Server ────────────────
  # Course Service does NOT generate tokens — it only VALIDATES them
  # Keycloak's JWK Set URI is used to verify JWT signatures (no DB needed!)
  security:
    oauth2:
      resourceserver:
        jwt:
          # Keycloak JWK Set endpoint — Spring fetches public keys automatically
          jwk-set-uri: http://localhost:8180/realms/lms-realm/protocol/openid-connect/certs
          # issuer-uri enables token issuer validation (recommended for production)
          issuer-uri: http://localhost:8180/realms/lms-realm

# ── Observability ───────────────────────────────────────────────────
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,loggers
  endpoint:
    health:
      show-details: always
  # Micrometer Tracing → OTel SDK → Jaeger
  tracing:
    sampling:
      probability: 1.0   # 100% in dev; use 0.1 in production
  # OTLP Exporter config (sends to Jaeger OTLP receiver)
  otlp:
    tracing:
      endpoint: http://localhost:4318/v1/traces   # OTLP HTTP
      timeout: 5s
    metrics:
      export:
        step: 10s

# ── OpenTelemetry Resource Attributes ──────────────────────────────
otel:
  service:
    name: ${spring.application.name}
  exporter:
    otlp:
      endpoint: http://localhost:4318
      protocol: http/protobuf
  propagators: tracecontext,baggage   # W3C Trace Context (standard)
  instrumentation:
    spring-web:
      enabled: true
    spring-webmvc:
      enabled: true
    jdbc:
      enabled: true

# ── Logging with Trace IDs injected automatically ──────────────────
logging:
  pattern:
    level: "%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]"
  level:
    "[org.springframework.security]": DEBUG   # remove in production
    "[org.hibernate.SQL]": DEBUG
    "[com.lms]": DEBUG
```

---

## PHẦN 3: COURSE SERVICE — `SecurityConfig.java`

```java
package com.lms.courseservice.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)   // ← Enables @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakJwtConverter keycloakJwtConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints — anyone can browse the course catalog
                .requestMatchers(HttpMethod.GET, "/api/courses", "/api/courses/{slug}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/courses/*/curriculum").permitAll()
                // Actuator health/info are public
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            // Configure as OAuth2 Resource Server — validates JWT from Keycloak
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter))
            );

        return http.build();
    }
}
```

---

## PHẦN 3: COURSE SERVICE — `KeycloakJwtConverter.java`

```java
package com.lms.courseservice.infrastructure.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts Keycloak JWT token into Spring Security Authentication.
 *
 * Keycloak embeds roles in: jwt.realm_access.roles
 * This converter extracts them and maps to "ROLE_INSTRUCTOR", "ROLE_STUDENT", etc.
 *
 * Usage in @PreAuthorize: hasRole('INSTRUCTOR')  → Spring checks "ROLE_INSTRUCTOR"
 */
@Component
public class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    // Default converter handles scope-based authorities (optional)
    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Merge default scope authorities + Keycloak realm roles
        Collection<GrantedAuthority> authorities = Stream.concat(
            defaultConverter.convert(jwt).stream(),
            extractRealmRoles(jwt).stream()
        ).collect(Collectors.toSet());

        // Use email as principal name (or "sub" if preferred)
        String principalName = jwt.getClaimAsString("email") != null
            ? jwt.getClaimAsString("email")
            : jwt.getSubject();

        return new JwtAuthenticationToken(jwt, authorities, principalName);
    }

    /**
     * Extracts roles from Keycloak's realm_access claim.
     *
     * JWT structure:
     * {
     *   "realm_access": {
     *     "roles": ["INSTRUCTOR", "ADMIN", "offline_access", "uma_authorization"]
     *   }
     * }
     */
    @SuppressWarnings("unchecked")
    private Set<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return Set.of();
        }

        List<String> roles = (List<String>) realmAccess.get("roles");

        return roles.stream()
            // Filter out Keycloak's internal roles
            .filter(role -> !role.equals("offline_access") && !role.equals("uma_authorization"))
            // Spring Security convention: prefix with "ROLE_"
            // @PreAuthorize("hasRole('INSTRUCTOR')") checks for "ROLE_INSTRUCTOR"
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
            .collect(Collectors.toSet());
    }
}
```

---

## PHẦN 4: HƯỚNG DẪN DI DỜI CODE (Migration Guide)

### 4.1 — Bảng ánh xạ file hiện tại → vị trí mới

| File hiện tại (Monolith) | File mới (Microservice) | Tầng CA |
|--------------------------|------------------------|---------|
| `modules/course/entity/Course.java` | `domain/model/Course.java` | Domain |
| `modules/course/entity/Module.java` | `domain/model/Module.java` | Domain |
| `modules/course/entity/Lesson.java` | `domain/model/Lesson.java` | Domain |
| `modules/course/entity/Category.java` | `domain/model/Category.java` | Domain |
| `modules/course/enums/CourseStatus.java` | `domain/enums/CourseStatus.java` | Domain |
| `modules/course/repository/CourseRepository.java` | `adapter/out/persistence/CourseJpaRepository.java` | Adapter |
| `modules/course/repository/ModuleRepository.java` | `adapter/out/persistence/ModuleJpaRepository.java` | Adapter |
| `modules/course/dto/CourseResponse.java` | `adapter/in/rest/dto/CourseResponse.java` | Adapter |
| `modules/course/dto/CourseUpsertRequest.java` | `adapter/in/rest/dto/CourseUpsertRequest.java` | Adapter |
| `modules/course/mapper/CourseMapper.java` | `adapter/out/persistence/mapper/CourseMapper.java` | Adapter |
| `modules/course/service/CourseService.java` | `application/port/in/GetCourseUseCase.java` | Application |
| `modules/course/service/impl/CourseServiceImpl.java` | `application/service/CourseApplicationService.java` | Application |
| `modules/course/controller/CourseController.java` | `adapter/in/rest/CourseController.java` | Adapter |
| `common/exception/GlobalExceptionHandler.java` | `infrastructure/config/GlobalExceptionHandler.java` | Infra |
| `common/entity/AuditableEntity.java` | `domain/model/AuditableEntity.java` hoặc shared-kernel | Domain/Shared |

### 4.2 — Thay đổi cần thiết khi migrate

#### ❶ Entity: Không thay đổi code — chỉ di chuyển package

```java
// Chỉ thay đổi dòng package:
// TRƯỚC: package com.lms.modules.course.entity;
// SAU:   package com.lms.courseservice.domain.model;
```

#### ❷ Repository: Đổi tên + package

```java
// TRƯỚC: CourseRepository.java (com.lms.modules.course.repository)
// SAU:   CourseJpaRepository.java (com.lms.courseservice.adapter.out.persistence)
// Nội dung giữ nguyên 100%

@Repository
public interface CourseJpaRepository extends JpaRepository<Course, Long> {
    Optional<Course> findBySlug(String slug);
    List<Course> findAllByStatus(CourseStatus status);
}
```

#### ❸ Service: Đổi tên class + xóa dependency với auth module

```java
// TRƯỚC: inject UserRepository từ module auth để lấy Instructor
// SAU:   nhận instructorId từ JWT token (không cần gọi User service)

// Trong CourseApplicationService.createCourse():
// instructorId đến từ: SecurityContextHolder → JwtAuthenticationToken → jwt.getClaim("sub")
// KHÔNG cần cross-service call!

@PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
public CourseResponse createCourse(CourseUpsertRequest request) {
    // Lấy instructorId từ JWT (không cần DB call, không cần User service)
    JwtAuthenticationToken auth = (JwtAuthenticationToken)
        SecurityContextHolder.getContext().getAuthentication();
    String instructorKeycloakId = auth.getToken().getSubject();
    Long instructorId = ...; // parse or store as String in Course
}
```

#### ❹ SecurityConfig: Thay JWT tự build → Keycloak Resource Server

```java
// XÓA HOÀN TOÀN:
// - JwtTokenProvider.java       (chúng ta không tạo token nữa)
// - CustomUserDetails.java       (Keycloak quản lý users)
// - AppJwtProperties.java        (không còn secret key)
// - JwtConfig.java               (không còn NimbusJwtEncoder)
//
// GIỮ LẠI:
// - GlobalExceptionHandler.java
// - CustomAccessDeniedHandler.java  (vẫn dùng)
// - CustomAuthenticationEntryPoint.java
//
// THÊM MỚI:
// - KeycloakJwtConverter.java    (đã có ở trên)
// - SecurityConfig.java          (đã có ở trên)
```

#### ❺ Flyway Migration: Chỉ giữ các bảng liên quan đến Course

```sql
-- Trong course-service, chỉ migrate các bảng:
-- categories, courses, modules, lessons, lesson_resources

-- XÓA khỏi migration của course-service:
-- users, roles, permissions, user_roles, role_permissions, refresh_tokens
-- enrollments, course_progress, lesson_progress (→ enrollment-service)
-- quizzes, questions, ... (→ quiz-service)
```

### 4.3 — Quy trình migrate từng bước

```bash
# BƯỚC 1: Tạo Maven parent project
mkdir lms-platform && cd lms-platform
# Copy pom.xml parent như trên

# BƯỚC 2: Tạo course-service module
mkdir course-service
# Copy pom.xml course-service

# BƯỚC 3: Di chuyển files
# Copy từng folder, chỉ update dòng "package" ở đầu file

# BƯỚC 4: Tạo migration SQL riêng cho course-service
# Chỉ chứa: categories, courses, modules, lessons, lesson_resources

# BƯỚC 5: Test compile
mvn clean compile -pl course-service

# BƯỚC 6: Start infrastructure
docker-compose up -d postgres keycloak jaeger

# BƯỚC 7: Verify Keycloak ready
curl http://localhost:8180/realms/lms-realm/.well-known/openid-configuration

# BƯỚC 8: Start course-service
mvn spring-boot:run -pl course-service

# BƯỚC 9: Test with JWT from Keycloak
TOKEN=$(curl -s -X POST http://localhost:8180/realms/lms-realm/protocol/openid-connect/token \
  -d 'grant_type=password' \
  -d 'client_id=lms-api-gateway' \
  -d 'username=instructor@lms.edu.vn' \
  -d 'password=123456' | jq -r '.access_token')

curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/courses
```

---

## PHẦN 4: HƯỚNG DẪN KEYCLOAK ADMIN CONSOLE

### Quick Setup (Manual nếu auto-import lỗi)

```
URL: http://localhost:8180/admin
User: admin / admin

1. Create Realm:
   Left panel → "Create realm" → Name: "lms-realm" → Create

2. Create Roles:
   Realm Settings → Roles → "Create role"
   → ADMIN, INSTRUCTOR, STUDENT

3. Create Client (Frontend):
   Clients → Create client
   → Client ID: lms-api-gateway
   → Client type: OpenID Connect
   → Authentication: OFF (Public client)
   → Valid redirect URIs: http://localhost:3000/*
   → Direct access grants: ON (for Postman testing)

4. Create Client (Backend — Resource Server):
   Client ID: course-service
   → Client authentication: ON (Confidential)
   → Service accounts: ON
   → Direct access grants: OFF

5. Create Test User:
   Users → Add user → username: instructor@lms.edu.vn
   → Credentials tab → Set password: 123456 (Temporary: OFF)
   → Role mappings → Assign INSTRUCTOR role

6. Get Token (test in Postman / curl):
   POST http://localhost:8180/realms/lms-realm/protocol/openid-connect/token
   Body (form-data):
     grant_type = password
     client_id  = lms-api-gateway
     username   = instructor@lms.edu.vn
     password   = 123456
```

---

## QUICK START CHECKLIST

```
[ ] docker-compose up -d postgres
[ ] docker-compose up -d keycloak        (wait ~90s for startup)
[ ] docker-compose up -d jaeger prometheus grafana
[ ] Verify: http://localhost:8180/admin  → Keycloak UP
[ ] Verify: http://localhost:16686       → Jaeger UI UP
[ ] Verify: http://localhost:9090        → Prometheus UP
[ ] Verify: http://localhost:3001        → Grafana UP (admin/admin)
[ ] GET http://localhost:8180/realms/lms-realm/.well-known/openid-configuration
[ ] Start course-service: mvn spring-boot:run
[ ] GET http://localhost:8081/actuator/health → {"status":"UP"}
[ ] GET http://localhost:8081/api/courses     → 200 OK (public)
[ ] POST Keycloak for token → GET /api/courses with Bearer token
[ ] Open Jaeger UI → Find traces from "course-service"
```
